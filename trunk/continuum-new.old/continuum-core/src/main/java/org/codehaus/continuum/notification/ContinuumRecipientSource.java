package org.codehaus.continuum.notification;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
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
 * @version $Id: ContinuumRecipientSource.java,v 1.1 2005-03-09 20:06:42 trygvis Exp $
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
