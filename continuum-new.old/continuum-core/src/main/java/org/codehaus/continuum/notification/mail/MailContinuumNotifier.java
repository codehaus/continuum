package org.codehaus.continuum.notification.mail;

/*
 * The MIT License
 *
 * Copyright (c) 2004, Jason van Zyl and Trygve Laugst�l
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.notification.AbstractContinuumNotifier;
import org.codehaus.continuum.project.ContinuumProjectState;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.store.ContinuumStore;
import org.codehaus.continuum.store.ContinuumStoreException;
import org.codehaus.continuum.utils.PlexusUtils;
import org.codehaus.plexus.mailsender.MailSender;
import org.codehaus.plexus.mailsender.MailSenderException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: MailContinuumNotifier.java,v 1.2 2005-02-21 14:58:10 trygvis Exp $
 */
public class MailContinuumNotifier
    extends AbstractContinuumNotifier
    implements Initializable
{
    private ContinuumStore store;

    private MailSender mailSender;

    private String to;

    private String administrator;

    private String from;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private String fromName;

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
        catch ( UnknownHostException ex )
        {
            fromName = "continuum";
        }

        fromName = "continuum@" + localHostName;

        getLogger().info( "From name: " + fromName );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

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

        if ( !generators.containsKey( project.getBuilderId() ) )
        {
            getLogger().warn( "Uknown project type: '" + project.getBuilderId() + "'." );

            return;
        }

        // ----------------------------------------------------------------------
        // Check if the mail should be sent at all
        // ----------------------------------------------------------------------

        ContinuumBuild lastBuild = getLastBuild( project, build );

        if ( shouldNotify( project, build, lastBuild ) )
        {
            MailGenerator generator = (MailGenerator) generators.get( project.getBuilderId() );

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
        catch ( MailSenderException ex )
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

        if ( build.getState() != lastBuild.getState() )
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
        catch ( ContinuumStoreException ex )
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
