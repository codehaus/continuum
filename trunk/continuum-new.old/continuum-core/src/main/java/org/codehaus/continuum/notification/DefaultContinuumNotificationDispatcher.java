package org.codehaus.continuum.notification;

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

import java.util.HashMap;
import java.util.Map;

import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.store.ContinuumStore;
import org.codehaus.continuum.store.ContinuumStoreException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.notification.NotificationDispatcher;
import org.codehaus.plexus.notification.NotificationException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultContinuumNotificationDispatcher.java,v 1.1 2005-03-09 20:06:42 trygvis Exp $
 */
public class DefaultContinuumNotificationDispatcher
    extends AbstractLogEnabled
    implements ContinuumNotificationDispatcher
{
    /** @requirement */
    private NotificationDispatcher notificationDispatcher;

    /** @requirement */
    private ContinuumStore store;

    // ----------------------------------------------------------------------
    // ContinuumNotificationDispatcher Implementation
    // ----------------------------------------------------------------------

    public void buildStarted( ContinuumBuild build )
    {
        sendNotifiaction( MESSAGE_ID_BUILD_STARTED, build );
    }

    public void checkoutStarted( ContinuumBuild build )
    {
        sendNotifiaction( MESSAGE_ID_CHECKOUT_STARTED, build );
    }

    public void checkoutComplete( ContinuumBuild build )
    {
        sendNotifiaction( MESSAGE_ID_CHECKOUT_COMPLETE, build );
    }

    public void runningGoals( ContinuumBuild build )
    {
        sendNotifiaction( MESSAGE_ID_RUNNING_GOALS, build );
    }

    public void goalsCompleted( ContinuumBuild build )
    {
        sendNotifiaction( MESSAGE_ID_GOALS_COMPLETED, build );
    }

    public void buildComplete( ContinuumBuild build )
    {
        sendNotifiaction( MESSAGE_ID_BUILD_COMPLETE, build );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private void sendNotifiaction( String messageId, ContinuumBuild build )
    {
        Map context = new HashMap();

        context.put( CONTEXT_BUILD, build );

        try
        {
            context.put( CONTEXT_PROJECT, store.getProject( build.getProject().getId() ) );
        }
        catch ( ContinuumStoreException e )
        {
            getLogger().error( "Error while loading the project.", e );

            return;
        }

        try
        {
            notificationDispatcher.sendNotification( messageId, context );
        }
        catch ( NotificationException e )
        {
            getLogger().error( "Error while notifying.", e );
        }
    }
}
