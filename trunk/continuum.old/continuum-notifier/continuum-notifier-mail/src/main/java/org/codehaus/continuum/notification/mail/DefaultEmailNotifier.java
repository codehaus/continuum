package org.codehaus.plexus.continuum.notification.mail;

import org.apache.maven.project.MavenProject;

import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.continuum.ContinuumException;
import org.codehaus.plexus.continuum.mail.MailMessage;
import org.codehaus.plexus.continuum.notification.ContinuumNotifier;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: DefaultEmailNotifier.java,v 1.2 2004-04-07 15:56:56 trygvis Exp $
 */
public class DefaultEmailNotifier
    extends AbstractLogEnabled
    implements Initializable, ContinuumNotifier
{
    private String smtpServer;

    private String replyTo;

    public void initialize()
        throws Exception
    {
        if ( smtpServer == null )
            throw new PlexusConfigurationException( "Missing configuration element: smtp server." );

        if ( replyTo == null )
            getLogger().warn( "Reply to is not configured, will use the nag email address from the project." );
    }
    
    public void notifyAudience( MavenProject project, String message )
        throws Exception
    {
        getLogger().info( "Sending message to: " + smtpServer );

        MailMessage mailMessage = new MailMessage( smtpServer );

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

        mailMessage.setSubject( "Continuum: " + project.getName() );

        mailMessage.getPrintStream().print( message );

        mailMessage.sendAndClose();

        getLogger().info( "The following message has been sent: " );

        getLogger().info( message );
    }
}
