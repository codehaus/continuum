package org.codehaus.continuum.notification.mail;

/*
 * LICENSE
 */

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.notification.AbstractContinuumNotifier;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ContinuumProjectState;
import org.codehaus.continuum.store.ContinuumStore;
import org.codehaus.continuum.store.ContinuumStoreException;
import org.codehaus.continuum.utils.PlexusUtils;
import org.codehaus.plexus.mailsender.MailSender;
import org.codehaus.plexus.mailsender.MailSenderException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: MailContinuumNotifier.java,v 1.14 2004-10-28 21:21:41 trygvis Exp $
 */
public class MailContinuumNotifier
    extends AbstractContinuumNotifier
    implements Initializable
{
    /** @requirement */
    private ContinuumStore store;

    /** @requirement */
    private MailSender mailSender;

//    /**
//     * The hostname of the SMTP server.
//     * 
//     * @default localhost
//     */
//    private String smtpServer;
//
//    /**
//     * 
//     * @default 25
//     */
//    private Integer smtpPort;

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

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private String fromName;

//    private int port;

    private String localHostName;

    private Map generators = new HashMap();

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws Exception
    {
        PlexusUtils.assertRequirement( store, "store" );
        PlexusUtils.assertRequirement( mailSender, "mail-sender" );

//        PlexusUtils.assertConfiguration( smtpServer, "smtp-server" );

        // ----------------------------------------------------------------------
        // Administrator
        // ----------------------------------------------------------------------

        if ( StringUtils.isEmpty( administrator ) )
        {
            getLogger().warn( "No administrator email address configured." );

            administrator = null;
        }

        // ----------------------------------------------------------------------
        // To address
        // ----------------------------------------------------------------------

        if ( StringUtils.isEmpty( to ) )
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

        if ( StringUtils.isEmpty( from ) )
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

            localHostName = address.getCanonicalHostName();
        }
        catch( UnknownHostException ex )
        {
            fromName = "continuum";
        }

        fromName = "continuum@" + localHostName;

        getLogger().info( "From name: " + fromName );

        // ----------------------------------------------------------------------
        // Smtp port
        // ----------------------------------------------------------------------

//        if ( smtpPort == null )
//        {
//            port = 25;
//            getLogger().info( "Smtp port is not configured, will use port 25." );
//        }
//        else
//        {
//            port = smtpPort.intValue();
//            getLogger().info( "Smtp port: " + port );
//        }

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
            getLogger().warn( "Uknown project type: '" + project.getType() + "'." );

            return;
        }

        // ----------------------------------------------------------------------
        // Check if the mail should be sent at all
        // ----------------------------------------------------------------------

        ContinuumBuild lastBuild = getLastBuild( project, build );

        if ( shouldNotify( project, build, lastBuild ) )
        {
            MailGenerator generator = (MailGenerator) generators.get( project.getType() );

            String message = generator.generateContent( project, build, lastBuild );

            String subject = generator.generateSubject( project, build, lastBuild );

            sendMessage( build, subject, message );
        }
    }

    // ----------------------------------------------------------------------
    // Private
    // ----------------------------------------------------------------------

    private void sendMessage( ContinuumBuild build, String subject, String message )
        throws ContinuumException
    {
        ContinuumProject project = build.getProject();

        String fromAddress = getFromAddress( project );

        if ( fromAddress == null )
        {
            getLogger().warn( project.getName() + ": Project is missing nag email and global from address is missing, not sending mail." );

            return;
        }

//        if ( fromName != null )
//        {
//            from = fromName + "<" + from + ">";
//        }

        String toAddress = getToAddress( project );

        if ( toAddress == null )
        {
            getLogger().warn( project.getName() + ": Project is missing nag email and global from address is missing, not sending mail." );

            return;
        }

        getLogger().info( "Sending message: From '" + from + "'. To '" + toAddress + "'." );

        Map headers = new HashMap();

        headers.put( "X-Continuum-Host", localHostName );

        try
        {
            mailSender.send( subject, message, toAddress, null, fromAddress, fromName, headers );
        }
        catch( MailSenderException ex )
        {
            throw new ContinuumException( "Exception while sending message.", ex );
        }
    }

    private String getFromAddress( ContinuumProject project )
        throws ContinuumException
    {
        if ( from != null )
        {
            return from;
        }

        if ( StringUtils.isEmpty( project.getNagEmailAddress() ) )
        {
            return "Continuum Internal Mail Service <continuum>";
        }

        return project.getNagEmailAddress();
    }

    private String getToAddress( ContinuumProject project )
        throws ContinuumException
    {
        if ( to != null )
        {
            return to;
        }

        if ( StringUtils.isEmpty( project.getNagEmailAddress() ) )
        {
            return administrator;
        }

        return project.getNagEmailAddress();
    }

    private boolean shouldNotify( ContinuumProject project, ContinuumBuild build, ContinuumBuild lastBuild )
    	throws ContinuumException
    {
        // Always send if the project failed
        if ( build.getState() == ContinuumProjectState.FAILED )
        {
            return true;
        }

        // Send if this is the first build
        if ( lastBuild == null )
        {
            return true;
        }

        // Send if the state has changed
        getLogger().info( "Current build state: " + build.getState() + ", last build state: " + lastBuild.getState() );

        if ( !build.getState().equals( lastBuild.getState() ) )
        {
            return true;
        }

        getLogger().info( "Same state, not sending mail." );

        return false;
    }

    private ContinuumBuild getLastBuild( ContinuumProject project, ContinuumBuild currentBuild )
    	throws ContinuumException
    {
        Iterator it;

        getLogger().info( "Current build: " + currentBuild.getId() );

        try
        {
            it = store.getBuildsForProject( project.getId(), 0, 0 );
        }
        catch( ContinuumStoreException ex )
        {
            throw new ContinuumException( "Error while finding the last project build." );
        }

        if ( !it.hasNext() )
        {
            return null;
        }

        ContinuumBuild build = (ContinuumBuild) it.next();

        if ( build.getId() != currentBuild.getId() )
        {
            throw new ContinuumException( "INTERNAL ERROR: The current build wasn't the first in the build list" );
        }

        if ( !it.hasNext() )
        {
            return null;
        }

        return (ContinuumBuild) it.next();
    }
}
