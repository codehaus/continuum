package org.codehaus.continuum.notification.mail;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Date;

import org.apache.maven.model.CiManagement;
import org.apache.maven.project.MavenProject;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.builder.maven2.Maven2BuildResult;
import org.codehaus.continuum.builder.maven2.Maven2ProjectDescriptor;
import org.codehaus.continuum.mail.MailMessage;
import org.codehaus.continuum.notification.AbstractContinuumNotifier;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ContinuumProjectState;
import org.codehaus.continuum.utils.PlexusUtils;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: MailContinuumNotifier.java,v 1.8 2004-07-29 22:57:32 trygvis Exp $
 */
public class MailContinuumNotifier
    extends AbstractContinuumNotifier
    implements Initializable
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
     * The email address of the administrator.
     * 
     * @default
     */
    private String administrator;

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

        if ( administrator == null || administrator.trim().length() == 0 )
        {
            getLogger().warn( "No administrator email address configured." );

            administrator = null;
        }

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

    public void buildStarted( ContinuumBuild build )
        throws ContinuumException
    {
    }

    public void checkoutStarted( ContinuumBuild build )
        throws ContinuumException
    {
    }

    public void checkoutComplete( ContinuumBuild build )
        throws ContinuumException
    {
    }

    public void runningGoals( ContinuumBuild build )
        throws ContinuumException
    {
    }

    public void goalsCompleted( ContinuumBuild build )
        throws ContinuumException
    {
    }

    public void buildComplete( ContinuumBuild build )
        throws ContinuumException
    {
        StringWriter message = new StringWriter();

        PrintWriter output = new PrintWriter( message );

        ContinuumProject project = build.getProject();

        if ( !project.getType().equals( "maven2" ) )
        {
            throw new ContinuumException( "Uknown project type: '" + project.getType() + "'." );
        }

        Maven2BuildResult result = (Maven2BuildResult)build.getBuildResult();

        ContinuumProjectState state = build.getState();

        String subject;

        if ( state == ContinuumProjectState.ERROR )
        {
            output.println( "BUILD ERROR" );

            subject = "[continuum] BUILD ERROR: " + project.getName();

            Throwable error = build.getError();

            if ( error != null )
            {
                line( output );

                output.println( "Build exeption:" );

                line( output );

                error.printStackTrace( output );

                line( output );
            }
        }
        else if ( state == ContinuumProjectState.OK || state == ContinuumProjectState.FAILED )
        {
//            ExecutionResponse response = result.getExecutionResponse();
            String response = result.getExecutionResponse();
/*
            // TODO: add isExecutionError() in the repsponse
            if ( response.getException() != null )
            {
                subject = "[continuum] BUILD ERROR: " + project.getName();

                line( output );

                output.println( "BUILD ERROR" );

                line( output );

                Throwable error = response.getException();

                if ( error != null )
                {
                    output.println( "Build exeption:" );

                    line( output );

                    error.printStackTrace( output );

                    line( output );
                }
            }
            else */
            if ( !result.isSuccess() )
            {
                subject = "[continuum] BUILD FAILURE: " + project.getName();

                line( output );

                output.println( "BUILD FAILURE" );

                line( output );

                output.println( "Reason: " + result.getShortFailureResponse() );

                output.println( result.getLongFailureResponse() );

                line( output );

                output.println( "Execution response: " );

                output.println( response );

                writeStats( output, build );
            }
            else
            {
                subject = "[continuum] BUILD SUCCESSFUL: " + project.getName();

                line( output );

                output.println( "BUILD SUCCESSFUL" );

                if ( response.trim().length() > 0 )
                {
                    line( output );

                    output.println( response );
                }
                writeStats( output, build );
            }
        }
        else
        {
            getLogger().warn( "Unexpected build state: " + state );

            subject = "[continuum] BUILD ERROR: Unknown build state";
        }

        output.close();

        sendMessage( build, subject, message.toString() );
    }

    // ----------------------------------------------------------------------
    // Private
    // ----------------------------------------------------------------------

    private void sendMessage( ContinuumBuild build, String subject, String message )
        throws ContinuumException
    {
        getLogger().info( "Sending message to: " + smtpServer + ":" + port );

        ContinuumProject project = build.getProject();

//        MavenProject mavenProject = getMavenProject( project );

        try
        {
            MailMessage mailMessage = new MailMessage( smtpServer, port );

            String from = getFromAddress( project );

            if ( from == null )
            {
                getLogger().warn( project.getName() + ": Project is missing nag email and global from address is missing, not sending mail." );

                return;
            }

            mailMessage.from( from );

            String to = getToAddress( project );

            if ( to == null )
            {
                getLogger().warn( project.getName() + ": Project is missing nag email and global from address is missing, not sending mail." );

                return;
            }

            mailMessage.to( to );
/*
            ExecutionResponse response = (ExecutionResponse)build.getBuildResult();

            if ( response == null )
            {
                mailMessage.setSubject( "[continuum] BUILD ERROR: " + project.getName() );
            }
            else if ( response.isExecutionFailure() )
            {
                mailMessage.setSubject( "[continuum] BUILD UNSUCCESSFUL: " + project.getName() );
            }
            else
            {
                mailMessage.setSubject( "[continuum] BUILD SUCCESSFUL: " + project.getName() );
            }
*/
            mailMessage.setSubject( subject );

            mailMessage.getPrintStream().print( message );

            mailMessage.sendAndClose();

            // TODO: remove me
//            getLogger().info( "The following message has been sent: " );

//            getLogger().info( message );
        }
        catch( IOException ex )
        {
            throw new ContinuumException( "Exception while sending message.", ex );
        }
    }

    private String getFromAddress( ContinuumProject project )
        throws ContinuumException
    {
        String address;

        MavenProject mavenProject = getMavenProject( project );

        if ( from != null )
        {
            return from;
        }

        if ( mavenProject == null )
        {
            return "Continuum Internal Mail Service <continuum>";
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

    private String getToAddress( ContinuumProject project )
        throws ContinuumException
    {
        String address;

        MavenProject mavenProject = getMavenProject( project );

        if ( to != null )
        {
            return to;
        }

        if ( mavenProject == null )
        {
            return administrator;
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
        Maven2ProjectDescriptor descriptor = (Maven2ProjectDescriptor) project.getDescriptor();

        if ( descriptor == null )
        {
            return null;
        }

        return descriptor.getMavenProject();
    }

    private void writeStats( PrintWriter output, ContinuumBuild build )
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
