package org.codehaus.continuum.notification.mail;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.maven.model.CiManagement;
import org.apache.maven.project.MavenProject;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.builder.maven2.Maven2ProjectDescriptor;
import org.codehaus.continuum.mail.MailMessage;
import org.codehaus.continuum.notification.AbstractContinuumNotifier;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ContinuumProjectState;
import org.codehaus.continuum.store.ContinuumStore;
import org.codehaus.continuum.store.ContinuumStoreException;
import org.codehaus.continuum.utils.PlexusUtils;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: MailContinuumNotifier.java,v 1.11 2004-10-06 14:08:10 trygvis Exp $
 */
public class MailContinuumNotifier
    extends AbstractContinuumNotifier
    implements Initializable
{
    /**
     * @requirement
     */
    private ContinuumStore store;

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

    private String fromName;

    private int port;

    /**
     * This is the last message sent by the notifier.
     * 
     * Probably mostly useful for testing.
     */
    private String lastMessage;

    /**
     * The number of messages created (not necessary sent).
     */
    private int messageCount;

    private Map generators = new HashMap();

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws Exception
    {
        PlexusUtils.assertRequirement( store, "store" );

        PlexusUtils.assertConfiguration( smtpServer, "smtp-server" );

        // ----------------------------------------------------------------------
        // Administrator
        // ----------------------------------------------------------------------

        if ( administrator == null || administrator.trim().length() == 0 )
        {
            getLogger().warn( "No administrator email address configured." );

            administrator = null;
        }

        // ----------------------------------------------------------------------
        // To address
        // ----------------------------------------------------------------------

        if ( to == null )
        {
            getLogger().info( "To address is not configured, will use the nag email address from the project." );
        }
        else
        {
            getLogger().info( "Using '" + to + "' as the to address for all emails." );
        }

        // ----------------------------------------------------------------------
        // From address
        // ----------------------------------------------------------------------

        if ( from == null )
        {
            getLogger().info( "From address is not configured, will use the nag email address from the project." );
        }
        else
        {
            getLogger().info( "Using '" + from + "' as the from address for all emails." );
        }

        try
        {
            InetAddress address = InetAddress.getLocalHost();

            fromName = "Continuum@" + address.getCanonicalHostName();
        }
        catch( UnknownHostException ex )
        {
            fromName = "Continuum";
        }

        getLogger().info( "From name: " + fromName );

        // ----------------------------------------------------------------------
        // Smtp port
        // ----------------------------------------------------------------------

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

        // ----------------------------------------------------------------------
        // 
        // ----------------------------------------------------------------------

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

        // ----------------------------------------------------------------------
        // Check if the mail should be sent at all
        // ----------------------------------------------------------------------

        Iterator it;

        try
        {
            it = store.getBuildsForProject( project.getId(), 0, 0 );
        }
        catch( ContinuumStoreException ex )
        {
            throw new ContinuumException( "Error while finding the last project build." );
        }

        ContinuumBuild lastBuild = null;

        if ( it.hasNext() && build.getState() != ContinuumProjectState.FAILED )
        {
            List list = new ArrayList();

            while( it.hasNext() )
            {
                list.add( it.next() );
            }

            if ( list.size() > 1 )
            {
                lastBuild = (ContinuumBuild)list.get( list.size() - 2 );

                // send if failed or the state has changed
                if ( build.getState() == lastBuild.getState() )
                {
                    return;
                }
            }
        }

        MailGenerator generator = (MailGenerator) generators.get( project.getType() );

        lastMessage = generator.generateContent( project, build, lastBuild );

        String subject = generator.generateSubject( project, build, lastBuild );

        sendMessage( build, subject, lastMessage );
    }

    public String getLastMessage()
    {
        return lastMessage;
    }

    public int getMessageCount()
    {
        return messageCount;
    }

    // ----------------------------------------------------------------------
    // Private
    // ----------------------------------------------------------------------

    private void sendMessage( ContinuumBuild build, String subject, String message )
        throws ContinuumException
    {
        ContinuumProject project = build.getProject();

        messageCount++;

        try
        {
            MailMessage mailMessage = new MailMessage( smtpServer, port );

            String from = getFromAddress( project );

            if ( from == null )
            {
                getLogger().warn( project.getName() + ": Project is missing nag email and global from address is missing, not sending mail." );

                return;
            }

//            if ( fromName != null )
//            {
//                from = fromName + "<" + from + ">";
//            }

            mailMessage.from( from );

            String to = getToAddress( project );

            if ( to == null )
            {
                getLogger().warn( project.getName() + ": Project is missing nag email and global from address is missing, not sending mail." );

                return;
            }

            mailMessage.to( to );

            getLogger().info( "Sending message: From '" + from + "'. To '" + to + "'. SMTP host: " + smtpServer + ":" + port );

            mailMessage.setSubject( subject );

            mailMessage.getPrintStream().print( message );

            mailMessage.sendAndClose();
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
}
