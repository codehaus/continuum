package org.codehaus.plexus.continuum.notification.mail;

import java.io.IOException;

import org.apache.maven.project.MavenProject;

import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.continuum.ContinuumException;
import org.codehaus.plexus.continuum.mail.MailMessage;
import org.codehaus.plexus.continuum.notification.ContinuumNotifier;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: DefaultEmailNotifier.java,v 1.3 2004-04-24 23:54:13 trygvis Exp $
 */
public class DefaultEmailNotifier
    extends AbstractLogEnabled
    implements Initializable, ContinuumNotifier
{
    // configuration

    private String smtpServer;

    private String replyTo;

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

        if ( replyTo == null )
            getLogger().warn( "Reply to is not configured, will use the nag email address from the project." );

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
        sendMessage( project, "build complete." );
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
    
            if ( replyTo != null )
            {
                mailMessage.from( replyTo );
            }
            else
            {
                String address = project.getBuild().getNagEmailAddress();
    
                if ( address == null || address.trim().length() == 0 )
                {
                    if ( replyTo == null )
                        throw new ContinuumException( "The project doesn't have a nag email and there is no default reply to address." );
                    else
                        address = replyTo;
                }
    
                mailMessage.from( address );
            }
    
            mailMessage.to( project.getBuild().getNagEmailAddress() );
    
            mailMessage.setSubject( "[continuum] " + project.getName() );
    
            mailMessage.getPrintStream().print( message );
    
            mailMessage.sendAndClose();
    
            getLogger().info( "The following message has been sent: " );
    
            getLogger().info( message );
        }
        catch( IOException ex )
        {
            throw new ContinuumException( "Exception while sending message.", ex );
        }
    }
}
