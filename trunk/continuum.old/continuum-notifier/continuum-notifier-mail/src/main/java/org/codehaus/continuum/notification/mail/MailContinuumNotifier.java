package org.codehaus.continuum.notification.mail;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Date;

import org.apache.maven.ExecutionResponse;
import org.apache.maven.model.CiManagement;
import org.apache.maven.project.MavenProject;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.builder.maven2.Maven2ProjectDescriptor;
import org.codehaus.continuum.mail.MailMessage;
import org.codehaus.continuum.notification.ContinuumNotifier;
import org.codehaus.continuum.project.BuildResult;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.utils.PlexusUtils;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: MailContinuumNotifier.java,v 1.4 2004-07-13 20:52:27 trygvis Exp $
 */
public class MailContinuumNotifier
    extends AbstractLogEnabled
    implements Initializable, ContinuumNotifier
{
    /**
     * The hostname of the SMTP server.
     * 
     * @default localhost
     */
    private String smtpServer;

    /**
     * If set; all emails will be send to this address. If not all the nag email
     * address from the pom will be used.
     * 
     * @default
     */
    private String to;

    /**
     * If set; all emails will be send from this address. If not all the nag email
     * address from the pom will be used.
     * 
     * @default
     */
    private String from;

    /**
     * 
     * @default 25
     */
    private Integer smtpPort;

    // members

    private int port;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws Exception
    {
        PlexusUtils.assertConfiguration( smtpServer, "smtp-server" );

        if ( to == null )
        {
            getLogger().info( "To address is not configured, will use the nag email address from the project." );
        }
        else
        {
            getLogger().info( "Using '" + to + "' as the to address for all emails." );
        }

        if ( from == null )
        {
            getLogger().info( "From address is not configured, will use the nag email address from the project." );
        }
        else
        {
            getLogger().info( "Using '" + from + "' as the from address for all emails." );
        }

        if ( smtpPort == null )
        {
            port = 25;
            getLogger().info( "Smtp port is not configured, will use port 25." );
        }
        else
        {
            port = smtpPort.intValue();
            getLogger().info( "Smtp port: " + port );
        }
    }

    // ----------------------------------------------------------------------
    // Notifier Implementation
    // ----------------------------------------------------------------------

    public void buildStarted( BuildResult build )
        throws ContinuumException
    {
    }

    public void checkoutStarted( BuildResult build )
        throws ContinuumException
    {
    }

    public void checkoutComplete( BuildResult build, Exception ex)
        throws ContinuumException
    {
    }

    public void runningGoals( BuildResult build )
        throws ContinuumException
    {
    }

    public void goalsCompleted( BuildResult build, Exception ex)
        throws ContinuumException
    {
    }

    public void buildComplete( BuildResult build, Exception ex)
        throws ContinuumException
    {
        StringWriter message = new StringWriter();

        PrintWriter output = new PrintWriter( message );

        ExecutionResponse response = build.getMaven2Result();

        if ( response == null )
        {
            output.println( "BUILD ERROR" );

            Throwable error = build.getError();

            if ( error != null )
            {
                line( output );

                output.println( "Build exeption:" );

                line( output );

                error.printStackTrace( output );

                line( output );
            }

            if ( ex != null )
            {
                line( output );

                output.println( "Build exeption:" );

                line( output );

                ex.printStackTrace( output );

                line( output );
            }
        }
        else
        {
            if ( !response.isExecutionFailure() )
            {
                output.println( "BUILD SUCCESSFUL" );
    
                writeStats( output, build );
            }
            else
            {
                output.println( "BUILD FAILURE" );
    
                output.println( "Reason: " + response.getFailureResponse().shortMessage() );
    
                output.println( response.getFailureResponse().longMessage() );
    
                writeStats( output, build );
            }
        }

        output.close();

        sendMessage( build, message.toString() );
    }

    // ----------------------------------------------------------------------
    // Private
    // ----------------------------------------------------------------------

    private void sendMessage( BuildResult build, String message )
        throws ContinuumException
    {
        getLogger().info( "Sending message to: " + smtpServer + ":" + port );

        ContinuumProject project = build.getProject();

        MavenProject mavenProject = getMavenProject( project );

        try
        {
            MailMessage mailMessage = new MailMessage( smtpServer, port );

            String from = getFromAddress( mavenProject );

            if ( from == null )
            {
                getLogger().warn( mavenProject.getGroupId() + ":" + mavenProject.getArtifactId() + ": Project is missing nag email and global from address is missing, not sending mail." );

                return;
            }

            mailMessage.from( from );

            String to = getToAddress( mavenProject );

            if ( to == null )
            {
                getLogger().warn( mavenProject.getGroupId() + ":" + mavenProject.getArtifactId() + ": Project is missing nag email and global from address is missing, not sending mail." );

                return;
            }

            mailMessage.to( to );

            if ( build.getMaven2Result().isExecutionFailure() )
            {
                mailMessage.setSubject( "[continuum] BUILD UNSUCCESSFUL: " + project.getName() );
            }
            else
            {
                mailMessage.setSubject( "[continuum] BUILD SUCCESSFUL: " + project.getName() );
            }

            mailMessage.getPrintStream().print( message );

//            mailMessage.sendAndClose();

            // TODO: remove me
            getLogger().info( "The following message has been sent: " );

            getLogger().info( message );
        }
        catch( IOException ex )
        {
            throw new ContinuumException( "Exception while sending message.", ex );
        }
    }

    private String getFromAddress( MavenProject mavenProject )
    {
        String address;

        if ( from != null )
        {
            return from;
        }

        CiManagement ciManagement = mavenProject.getCiManagement();

        if ( ciManagement == null )
        {
            return null;
        }

        address = StringUtils.trim( ciManagement.getNagEmailAddress() );

        if ( StringUtils.isEmpty( address ) )
        {
            return null;
        }

        return address;
    }

    private String getToAddress( MavenProject mavenProject )
    {
        String address;

        if ( to != null )
        {
            return to;
        }

        address = StringUtils.trim( mavenProject.getCiManagement().getNagEmailAddress() );

        if ( StringUtils.isEmpty( address ) )
        {
            return null;
        }

        return address;
    }

    private MavenProject getMavenProject( ContinuumProject project )
        throws ContinuumException
    {
        if ( !project.getType().equals( "maven2" ) )
        {
            throw new ContinuumException( "Uknown project type: '" + project.getType() + "'." );
        }

        Maven2ProjectDescriptor descriptor = (Maven2ProjectDescriptor) project.getDescriptor();

        return descriptor.getMavenProject();
    }

    private void writeStats( PrintWriter output, BuildResult build )
    {
        long fullDiff = build.getEndTime() - build.getStartTime();

        line( output );

        output.println( "Build statistics" );

        line( output );

        output.println( "Started at: " + formatTime( build.getStartTime() ) );

        output.println( "Finished at: " + formatTime( build.getEndTime() ) );

        output.println( "Total time: " + formatTimeInterval( fullDiff ) );

        line( output );
    }

    private void line( PrintWriter output )
    {
        output.println( "----------------------------------------------------------------------------" );
    }

    private String formatTime( long time )
    {
        return getDateFormat().format( new Date( time ) );
    }

    private static String formatTimeInterval( long ms )
    {
        long secs = ms / 1000;
        long min = secs / 60;
        secs = secs % 60;

        if ( min > 0 )
        {
            return min + " minutes " + secs + " seconds";
        }
        else
        {
            return secs + " seconds";
        }
    }

    private ThreadLocal dateFormatter = new ThreadLocal();

    private DateFormat getDateFormat()
    {
        DateFormat dateFormatter = (DateFormat) this.dateFormatter.get();

        if ( dateFormatter == null )
        {
            dateFormatter = DateFormat.getDateTimeInstance( DateFormat.FULL, DateFormat.FULL );

            this.dateFormatter.set( dateFormatter );
        }

        return dateFormatter;
    }
}
