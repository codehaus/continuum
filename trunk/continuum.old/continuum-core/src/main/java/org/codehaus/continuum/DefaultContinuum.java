package org.codehaus.plexus.continuum;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.Project;
import org.apache.maven.project.ProjectBuilder;
import org.codehaus.plexus.compiler.Compiler;
import org.codehaus.plexus.continuum.notification.mail.MailMessage;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.net.URL;
import java.net.MalformedURLException;

public class DefaultContinuum
    extends AbstractLogEnabled
    implements Continuum, Initializable, Startable
{
    private ProjectBuilder projectBuilder;

    private Compiler compiler;

    // Configuration

    /** Where all the sources get checked out to be built. */
    private String workingDirectory;

    // Anything mail related can be encapsualted in a separate
    // communicatino module but it's what we'll be doing first so we'll
    // get it working before abstracting it out.

    /** Outgoing smtp server for messages. */
    private String smtpServer;

    /** Message to set as the reply to in the outgoing messages. */
    private String replyTo;

    // Internal helpers

    private Map builds;

    public void initialize()
        throws Exception
    {
        builds = new LinkedHashMap();

        File f = new File( workingDirectory );

        if ( !f.exists() )
        {
            f.mkdirs();
        }
    }

    public void start()
        throws Exception
    {
    }

    public void stop()
        throws Exception
    {
    }

    public void addProject( String projectUrl )
    {
        // We will simply deal with POMs that can be retrieved from
        // the local file system or over http.

        Project project = null;

        if ( projectUrl.startsWith( "http://" ) )
        {
            try
            {
                URL url = new URL( projectUrl );

                InputStream is = url.openStream();

                byte[] buffer = new byte[1024];

                int read = 0;

                ByteArrayOutputStream os = new ByteArrayOutputStream();

                while( is.available() > 0 )
                {
                    read = is.read( buffer, 0, buffer.length );

                    if ( read < 0 )
                    {
                        break;
                    }

                    os.write( buffer, 0, read );
                }

                String pomContents = os.toString().trim();

                // Now we need to write this out to a temp file so that we
                // can read it because the project builder can only deal with
                // files because of configuration stuff.

            }
            catch ( Exception e )
            {
                getLogger().error( "Can't read POM from url: " + projectUrl, e );
            }
        }
        else
        {
            try
            {
                project = projectBuilder.build( new File( projectUrl ) );
            }
            catch ( Exception e )
            {
                getLogger().error( "Can't read POM from file system: " + projectUrl, e );
            }
        }

        // If we successfully created a project we're ready to go!
        if ( project != null )
        {
            addProject( project );
        }
    }

    public void addProject( Project project )
    {
        MavenProjectBuild build = null;

        try
        {
            build = new MavenProjectBuild( project );
        }
        catch ( Exception e )
        {
            getLogger().error( "Cannot add project!", e );
        }

        builds.put( project.getGroupId() + ":" + project.getArtifactId(), build );
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

    public void buildProject( String groupId, String artifactId )
        throws Exception
    {
        buildProject( (MavenProjectBuild) builds.get( groupId + ":" + artifactId ) );
    }

    public void buildProjects()
        throws Exception
    {
        getLogger().info( "Building Projects ..." );

        for ( Iterator i = builds.values().iterator(); i.hasNext(); )
        {
            MavenProjectBuild projectBuild = (MavenProjectBuild) i.next();

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

    private List buildProject( MavenProjectBuild build )
        throws Exception
    {
        // We need to check out the sources
        build.getProjectScm().checkout( workingDirectory );

        System.out.println( "build.getProject().getSourceDirectory() = " + build.getProject().getBuild().getSourceDirectory() );

        String destinationDirectory = workingDirectory + "/target/classes";

        List messages = compiler.compile( classpathElements( build.getProject() ),
                                          new String[]{build.getProject().getBuild().getSourceDirectory()},
                                          destinationDirectory );

        return messages;
    }

    private String[] classpathElements( Project project )
    {
        String[] classpathElements = new String[project.getArtifacts().size()];

        for ( int i = 0; i < classpathElements.length; i++ )
        {
            classpathElements[i] = ( (Artifact) project.getArtifacts().get( i ) ).getPath();
        }

        return classpathElements;
    }
}
