package org.codehaus.continuum.notification.mail;

/*
 * Copyright 2004-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.ResourceNotFoundException;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.notification.ContinuumNotificationDispatcher;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumBuildResult;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ContinuumProjectState;
import org.codehaus.continuum.store.ContinuumStore;
import org.codehaus.continuum.store.ContinuumStoreException;
import org.codehaus.continuum.utils.PlexusUtils;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.mailsender.MailMessage;
import org.codehaus.plexus.mailsender.MailSender;
import org.codehaus.plexus.mailsender.MailSenderException;
import org.codehaus.plexus.notification.NotificationException;
import org.codehaus.plexus.notification.notifier.Notifier;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.velocity.VelocityComponent;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: MailContinuumNotifier.java,v 1.4 2005-03-10 00:05:52 trygvis Exp $
 */
public class MailContinuumNotifier
    extends AbstractLogEnabled
    implements Initializable, Notifier
{
    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    /** @requirement */
    private VelocityComponent velocity;

    /** @configuration */
    private ContinuumStore store;

    /** @configuration */
    private MailSender mailSender;

    /** @configuration */
    private String from;

    /** @configuration */
    private String administrator;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private String fromName;

    private String localHostName;

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
    }

    // ----------------------------------------------------------------------
    // Notifier Implementation
    // ----------------------------------------------------------------------

    public void sendNotification( String source, Set recipients, Map context )
        throws NotificationException
    {
        ContinuumBuild build = (ContinuumBuild) context.get( ContinuumNotificationDispatcher.CONTEXT_BUILD );

        ContinuumProject project = (ContinuumProject) context.get( ContinuumNotificationDispatcher.CONTEXT_PROJECT );

        try
        {
            if ( source.equals( ContinuumNotificationDispatcher.MESSAGE_ID_BUILD_COMPLETE ) )
            {
                buildComplete( project, build, source, recipients );
            }
        }
        catch ( ContinuumException e )
        {
            throw new NotificationException( "Error while notifiying.", e );
        }
    }

    private void buildComplete( ContinuumProject project, ContinuumBuild build, String source, Set recipients )
        throws ContinuumException
    {
        // ----------------------------------------------------------------------
        // Check if the mail should be sent at all
        // ----------------------------------------------------------------------

        ContinuumBuild lastBuild = getPreviousBuild( project, build );

        if ( !shouldNotify( build, lastBuild ) )
        {
            return;
        }

        // ----------------------------------------------------------------------
        // Generate the mail contents
        // ----------------------------------------------------------------------

        String packageName = this.getClass().getPackage().getName().replace( '.', '/' );

        String templateName = "/" + packageName + "/templates/" + project.getBuilderId() + "/" + source + ".vm";

        StringWriter writer = new StringWriter();

        try
        {
            VelocityContext context = new VelocityContext();

            context.put( "build", build );

            context.put( "project", project );

            velocity.getEngine().mergeTemplate( templateName, context, writer );
        }
        catch ( ResourceNotFoundException e )
        {
            getLogger().info( "No such template: '" + templateName + "'." );

            return;
        }
        catch ( Exception e )
        {
            throw new ContinuumException( "Error while generating mail contents." , e );
        }

        // ----------------------------------------------------------------------
        // Send the mail
        // ----------------------------------------------------------------------

        String subject = generateSubject( project, build );

        sendMessage( build, recipients, subject, writer.getBuffer().toString() );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private String generateSubject( ContinuumProject project, ContinuumBuild build )
    {
        ContinuumBuildResult result = build.getBuildResult();

        int state = build.getState();

        if ( state == ContinuumProjectState.ERROR )
        {
            return "[continuum] BUILD ERROR: " + project.getName();
        }
        else if ( state == ContinuumProjectState.OK || state == ContinuumProjectState.FAILED )
        {
            if ( !result.isSuccess() )
            {
                return "[continuum] BUILD FAILURE: " + project.getName();
            }
            else
            {
                return "[continuum] BUILD SUCCESSFUL: " + project.getName();
            }
        }
        else
        {
            return "[continuum] ERROR: Unknown build state";
        }
    }

    private void sendMessage( ContinuumBuild build, Set recipients, String subject, String content )
        throws ContinuumException
    {
        ContinuumProject project = build.getProject();

        String fromAddress = getFromAddress( project );

        if ( fromAddress == null )
        {
            getLogger().warn( project.getName() + ": Project is missing nag email and global from address is missing, not sending mail." );

            return;
        }

        getLogger().info( "Sending message: From '" + from + "'." );

        MailMessage message = new MailMessage();

        message.addHeader( "X-Continuum-Host", localHostName );

        try
        {
            message.setSubject( subject );

            message.setContent( content );

            message.setFromAddress( fromAddress );

            message.setFromName( fromName );

            for ( Iterator it = recipients.iterator(); it.hasNext(); )
            {
                String recipient = (String) it.next();

                // TODO: set a proper name
                String name = recipient;

                message.addTo( name, recipient );
            }

            mailSender.send( message );
        }
        catch ( MailSenderException ex )
        {
            throw new ContinuumException( "Exception while sending message.", ex );
        }
    }

    private String getFromAddress( ContinuumProject project )
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

    private boolean shouldNotify( ContinuumBuild build, ContinuumBuild lastBuild )
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

    private ContinuumBuild getPreviousBuild( ContinuumProject project, ContinuumBuild currentBuild )
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
            throw new ContinuumException( "Error while finding the last project build.", ex );
        }

        if ( !it.hasNext() )
        {
            return null;
        }

        ContinuumBuild build = (ContinuumBuild) it.next();

        if ( !build.getId().equals( currentBuild.getId() ) )
        {
            throw new ContinuumException( "INTERNAL ERROR: The current build wasn't the first in the build list. " +
                                          "Current build: '" + currentBuild.getId() + "', " +
                                          "first build: '" + build.getId() + "'." );
        }

        if ( !it.hasNext() )
        {
            return null;
        }

        return (ContinuumBuild) it.next();
    }
}
