package org.codehaus.plexus.continuum;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.maven.project.Project;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.Model;
import org.codehaus.plexus.compiler.Compiler;
import org.codehaus.plexus.continuum.mail.MailMessage;
import org.codehaus.plexus.logging.AbstractLogEnabled;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: DefaultContinuum.java,v 1.3 2003-10-12 01:14:55 pdonald Exp $
 */
public class DefaultContinuum
    extends AbstractLogEnabled
    implements Continuum, Serviceable, Configurable, Initializable, Startable
{
    /** Maven Project Builder */
    private ProjectBuilder projectBuilder;

    /** Compiler */
    private Compiler compiler;

    /** TimerTask */
    private Timer timer;

    /** Build buildInterval */
    private int buildInterval;

    /** List of xml maven object model files */
    private List moms;

    /** List of projects to build. */
    private List projects;

    /** Working directory where builds take place. */
    private String workDirectory;

    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    /** @see Serviceable#service */
    public void service( ServiceManager serviceManager )
        throws ServiceException
    {
        projectBuilder = (ProjectBuilder) serviceManager.lookup( ProjectBuilder.ROLE );
        compiler = (Compiler) serviceManager.lookup( Compiler.ROLE );
    }

    /** @see Configurable#configure */
    public void configure( Configuration configuration )
        throws ConfigurationException
    {
        workDirectory = configuration.getChild( "work-directory" ).getValue();

        buildInterval = configuration.getChild( "build-interval" ).getValueAsInteger();

        moms = new ArrayList();

        Configuration[] momConfigurations = configuration.getChild( "moms" ).getChildren( "mom" );

        for ( int i = 0; i < momConfigurations.length; i++ )
        {
            Configuration momConfiguration = momConfigurations[i];

            moms.add( momConfiguration.getValue() );
        }
    }

    /** @see Initializable#initialize */
    public void initialize()
        throws Exception
    {
        // Create the work directory if it doesn't exist.
        File f = new File ( workDirectory );

        if ( !f.exists() )
        {
            f.mkdirs();
        }

        // Storage for projects.
        projects = new ArrayList();

        // Read in the moms and create projects.
        for ( Iterator i = moms.iterator(); i.hasNext(); )
        {
            String s = (String) i.next();

            File file = new File( s );

            if ( !file.exists() )
            {
                getLogger().warn( "The specified POM doesn't exist: " + file + ". Skipping." );
                continue;
            }

            Project project = null;

            try
            {
                project = projectBuilder.build( file );
                projects.add( project );
            }
            catch ( Exception e )
            {
                getLogger().warn( "Error building POM: " + file + ". Skipping." );
                continue;
            }
        }


        // Create the timer.
        timer = new Timer();
    }

    /** @see Startable#start */
    public void start()
        throws Exception
    {
        getLogger().info( "Starting Continuum!" );

        // Set the scheduler.
        timer.schedule( new BuildTask(), 0, buildInterval * 60 * 1000 );
    }

    /** @see Startable#stop */
    public void stop()
        throws Exception
    {
    }

    // ----------------------------------------------------------------------
    // Implementation
    // ----------------------------------------------------------------------

    public void addModel( Model model )
    {

    }

    private void notifyAudience( Project project, String message )
    {
        try
        {
            MailMessage mailMessage = new MailMessage( "mail.maven.org" );

            mailMessage.from( "jason@maven.org" );
            mailMessage.to( "jason@maven.org" );
            mailMessage.setSubject( "Continuum: " + project.getName() );
            mailMessage.getPrintStream().print( message );
            mailMessage.sendAndClose();

            getLogger().info( "The following message has been sent: " );
            getLogger().info( message );
        }
        catch ( IOException e )
        {
            getLogger().error( "Can't send notification message.", e );
        }
    }

    private void buildProjects()
    {
        getLogger().info( "Building Projects ..." );

        for ( Iterator i = projects.iterator(); i.hasNext(); )
        {
            Project project = (Project) i.next();

            try
            {
                List messages = compiler.compile( project );

                // Notification is there are failures.
                if ( messages.size() > 0 )
                {
                    StringBuffer message = new StringBuffer();

                    for ( Iterator j = messages.iterator(); j.hasNext(); )
                    {
                        message.append( j.next() ).append( "\n" );
                    }

                    notifyAudience( project, message.toString() );
                }
            }
            catch ( Exception e )
            {
                StringWriter writer = new StringWriter();
                PrintWriter w = new PrintWriter( writer );
                e.printStackTrace( w );

                // Notification of failure.
                notifyAudience( project, w.toString() );

            }
        }
    }

    /**
     *
     */
    class BuildTask
        extends TimerTask
    {
        public void run()
        {
            buildProjects();
        }
    }
}
