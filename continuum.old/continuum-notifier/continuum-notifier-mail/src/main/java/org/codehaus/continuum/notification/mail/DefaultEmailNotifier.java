package org.codehaus.continuum.notification.mail;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.maven.project.MavenProject;

import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.mail.MailMessage;
import org.codehaus.continuum.notification.ContinuumNotifier;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: DefaultEmailNotifier.java,v 1.5 2004-05-13 17:48:17 trygvis Exp $
 */
public class DefaultEmailNotifier
    extends AbstractLogEnabled
    implements Initializable, ContinuumNotifier
{
    // configuration

    private String smtpServer;

    /**
     * If set; all emails will be send to this address. If not all the nag email
     * address from the pom will be used.
     */
    private String to;

    /**
     * If set; all emails will be send from this address. If not all the nag email
     * address from the pom will be used.
     */
    private String from;

    private Integer smtpPort;

    // members

    private int port;

    ///////////////////////////////////////////////////////////////////////////
    // Lifecycle

    public void initialize()
        throws Exception
    {
        if ( smtpServer == null )
            throw new PlexusConfigurationException( "Missing configuration element: smtp server." );

        if ( to == null )
            getLogger().info( "To address is not configured, will use the nag email address from the project." );

        if ( from == null )
            getLogger().info( "From address is not configured, will use the nag email address from the project." );

        if ( smtpPort == null )
        {
            port = 25;
            getLogger().info( "Smtp port is not configured, will port 25." );
        }
        else
        {
            port = smtpPort.intValue();
            getLogger().info( "Smtp port: " + port );
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Notifier implementation

    public void buildStarted(MavenProject project)
        throws ContinuumException
    {
    }

    public void checkoutStarted(MavenProject project)
        throws ContinuumException
    {
    }

    public void checkoutComplete(MavenProject project, Exception ex)
        throws ContinuumException
    {
    }

    public void runningGoals(MavenProject project)
        throws ContinuumException
    {
    }

    public void goalsCompleted(MavenProject project, Exception ex)
        throws ContinuumException
    {
    }

    public void buildComplete(MavenProject project, Exception ex)
        throws ContinuumException
    {
        StringWriter message = new StringWriter();

        PrintWriter output = new PrintWriter( message );

        if ( ex == null )
        {
            output.println( "Build successfull." );
        }
        else
        {
            output.println( "Build failed." );
            output.println();
            ex.printStackTrace( output );
        }

        output.close();

        sendMessage( project, message.toString() );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Private

    private void sendMessage( MavenProject project, String message )
        throws ContinuumException
    {
        getLogger().info( "Sending message to: " + smtpServer + ":" + port );

        try
        {
            MailMessage mailMessage = new MailMessage( smtpServer, port );

            String from = getFromAddress( project );

            if ( from == null )
            {
                getLogger().warn( project.getGroupId() + ":" + project.getArtifactId() + ": Project is missing nag email and global from address is missing." );

                return;
            }

            mailMessage.from( from );

            String to = getToAddress( project );

            if ( to == null )
            {
                getLogger().warn( project.getGroupId() + ":" + project.getArtifactId() + ": Project is missing nag email and global from address is missing." );

                return;
            }

            mailMessage.to( to );
    
            mailMessage.setSubject( "[continuum] " + project.getName() );
    
            mailMessage.getPrintStream().print( message );

            mailMessage.sendAndClose();
/*
            // TODO: remove me
            getLogger().info( "The following message has been sent: " );

            getLogger().info( message );
*/
        }
        catch( IOException ex )
        {
            throw new ContinuumException( "Exception while sending message.", ex );
        }
    }

    private String getFromAddress( MavenProject project )
    {
        String address;

        if ( from != null )
            return from;

        address = StringUtils.trim( project.getBuild().getNagEmailAddress() );

        if ( StringUtils.isEmpty( address ) )
            return null;

        return address;
    }

    private String getToAddress( MavenProject project )
    {
        String address;

        if ( to != null )
            return to;

        address = StringUtils.trim( project.getBuild().getNagEmailAddress() );

        if ( StringUtils.isEmpty( address ) )
            return null;

        return address;
    }
}
