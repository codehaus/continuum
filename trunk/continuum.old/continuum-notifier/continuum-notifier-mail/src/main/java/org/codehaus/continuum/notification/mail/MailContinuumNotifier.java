package org.codehaus.continuum.notification.mail;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.model.CiManagement;
import org.apache.maven.project.MavenProject;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.builder.maven2.Maven2ProjectDescriptor;
import org.codehaus.continuum.mail.MailMessage;
import org.codehaus.continuum.notification.AbstractContinuumNotifier;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.utils.PlexusUtils;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: MailContinuumNotifier.java,v 1.9 2004-09-07 16:22:17 trygvis Exp $
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

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private int port;

    /**
     * This is the last message sent by the notifier.
     * 
     * Probably mostly useful for testing.
     */
    private String lastMessage;

    private Map generators = new HashMap();

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

        generators.put( "maven2", new Maven2MailGenerator( getLogger() ) );

        generators.put( "maven2", new ExternalMaven2MailGenerator( getLogger() ) );
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
        ContinuumProject project = build.getProject();

        if ( !generators.containsKey( project.getType() ) )
        {
            throw new ContinuumException( "Uknown project type: '" + project.getType() + "'." );
        }

        MailGenerator generator = (MailGenerator) generators.get( project.getType() );

        lastMessage = generator.generateContent( project, build );

        String subject = generator.generateSubject( project, build );

        sendMessage( build, subject, lastMessage );
    }

    public String getLastMessage()
    {
        return lastMessage;
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

        if ( mavenProject == null || mavenProject.getCiManagement() == null )
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
//        output.println( "----------------------------------------------------------------------------" );
        output.println( "****************************************************************************" );
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
