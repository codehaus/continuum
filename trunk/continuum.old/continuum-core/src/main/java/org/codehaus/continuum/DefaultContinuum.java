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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class DefaultContinuum
    extends AbstractLogEnabled
    implements Continuum, Initializable, Startable
{
    private ProjectBuilder projectBuilder;

    private Compiler compiler;

    // Configuration

    /** Where all the sources get checked out to be built. */
    private String workDirectory;

    /** How often are we going to attempt to build the project. */
    private int buildInterval;

    // Anything mail related can be encapsualted in a separate
    // communicatino module but it's what we'll be doing first so we'll
    // get it working before abstracting it out.

    /** Outgoing smtp server for messages. */
    private String smtpServer;

    /** Message to set as the reply to in the outgoing messages. */
    private String replyTo;

    // Internal helpers

    private Timer timer;

    private Map builds;

    public void initialize()
        throws Exception
    {
        builds = new LinkedHashMap();

        File f = new File ( workDirectory );

        if ( !f.exists() )
        {
            f.mkdirs();
        }

        timer = new Timer();
    }

    public void start()
        throws Exception
    {
        getLogger().info( "Starting Continuum!" );

        timer.schedule( new BuildTask(), 0, buildInterval * 60 * 1000 );
    }

    public void stop()
        throws Exception
    {
    }

    public void addProject( Project project )
    {
        ContinuumBuild build = new ContinuumBuild( project );

        builds.put( project.getId(), build );
    }

    private void notifyAudience( Project project, String message )
    {
        try
        {
            MailMessage mailMessage = new MailMessage( smtpServer );

            if ( replyTo != null )
            {
                mailMessage.from( replyTo );
            }
            else
            {
                mailMessage.from( project.getBuild().getNagEmailAddress() );
            }

            mailMessage.to( project.getBuild().getNagEmailAddress() );

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

        for ( Iterator i = builds.values().iterator(); i.hasNext(); )
        {
            ContinuumBuild projectBuild = (ContinuumBuild) i.next();

            try
            {
                List messages = buildProject( projectBuild );

                // Notification is there are failures.
                if ( messages.size() > 0 )
                {
                    StringBuffer message = new StringBuffer();

                    for ( Iterator j = messages.iterator(); j.hasNext(); )
                    {
                        message.append( j.next() ).append( "\n" );
                    }

                    notifyAudience( projectBuild.getProject(), message.toString() );
                }
            }
            catch ( Exception e )
            {
                StringWriter writer = new StringWriter();

                PrintWriter w = new PrintWriter( writer );

                e.printStackTrace( w );

                notifyAudience( projectBuild.getProject(), w.toString() );

            }
        }
    }

    private List buildProject( ContinuumBuild build )
        throws Exception
    {
        List messages = compiler.compile( classpathElements( build.getProject() ),
                                          new String[]{ build.getProject().getBuild().getSourceDirectory() },
                                          build.getProject().getProperty( "maven.build.dest" ) );

        return messages;
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
