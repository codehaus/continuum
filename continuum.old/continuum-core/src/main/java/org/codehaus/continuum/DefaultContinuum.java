package org.codehaus.plexus.continuum;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.Project;
import org.apache.maven.project.ProjectBuilder;
import org.codehaus.plexus.compiler.Compiler;
import org.codehaus.plexus.continuum.mail.MailMessage;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;

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
 * @version $Id: DefaultContinuum.java,v 1.5 2004-01-16 06:20:14 jvanzyl Exp $
 */
public class DefaultContinuum
    extends AbstractLogEnabled
    implements Continuum, Initializable, Startable
{
    private ProjectBuilder projectBuilder;

    private Compiler compiler;

    private Timer timer;

    private int buildInterval;

    private List moms;

    private List projects;

    private String workDirectory;

    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

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

    public void addProject( Project project )
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
                List messages = compiler.compile( classpathElements( project ),
                                                  new String[]{ project.getBuild().getSourceDirectory() },
                                                  project.getProperty( "maven.build.dest" ) );

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

    private String[] classpathElements( Project project )
    {
        String[] classpathElements = new String[project.getArtifacts().size()];

        for ( int i = 0; i < classpathElements.length; i++ )
        {
            classpathElements[i] = ((Artifact)project.getArtifacts().get(i)).getPath();
        }

        return classpathElements;
    }

    class BuildTask
        extends TimerTask
    {
        public void run()
        {
            buildProjects();
        }
    }
}
