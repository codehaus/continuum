package org.codehaus.continuum.notification;

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

import java.util.Set;
import java.util.Map;
import java.util.Collections;
import java.util.HashSet;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.notification.RecipientSource;
import org.codehaus.plexus.notification.NotificationException;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.continuum.project.ContinuumProject;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumRecipientSource.java,v 1.2 2005-03-10 00:05:51 trygvis Exp $
 */
public class ContinuumRecipientSource
    extends AbstractLogEnabled
    implements RecipientSource, Initializable
{
    /** @configuration */
    private String to;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws Exception
    {
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
    }

    // ----------------------------------------------------------------------
    // RecipientSource Implementation
    // ----------------------------------------------------------------------

    public Set getRecipients( String notifierType, String messageId, Map context )
        throws NotificationException
    {
        if ( notifierType.equals( "console" ) )
        {
            return getConsoleRecipients();
        }
        else if ( notifierType.equals( "mail" ) )
        {
            return getMailRecipients( context );
        }

        getLogger().warn( "Unknown notifier type '" + notifierType + "'." );

        return Collections.EMPTY_SET;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private Set getConsoleRecipients()
    {
        return Collections.EMPTY_SET;
    }

    private Set getMailRecipients( Map context )
    {
        ContinuumProject project = (ContinuumProject) context.get( ContinuumNotificationDispatcher.CONTEXT_PROJECT );

        Set recipients = new HashSet();

        if ( to != null )
        {
            recipients.add( to );
        }
        else if ( !StringUtils.isEmpty( project.getNagEmailAddress() ) )
        {
            recipients.add( project.getNagEmailAddress() );
        }

        return recipients;
    }
}