package org.codehaus.plexus.continuum.notification.mail;

import org.codehaus.plexus.continuum.notification.ContinuumNotifier;
import org.codehaus.plexus.continuum.mail.MailMessage;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.apache.maven.project.MavenProject;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: DefaultEmailNotifier.java,v 1.1 2004-02-02 20:36:19 jvanzyl Exp $
 */
public class DefaultEmailNotifier
    extends AbstractLogEnabled
    implements ContinuumNotifier
{
    private String smtpServer;

    private String replyTo;

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
            mailMessage.from( project.getBuild().getNagEmailAddress() );
        }

        mailMessage.to( project.getBuild().getNagEmailAddress() );

        mailMessage.setSubject( "Continuum: " + project.getName() );

        mailMessage.getPrintStream().print( message );

        mailMessage.sendAndClose();

        getLogger().info( "The following message has been sent: " );

        getLogger().info( message );
    }
}
