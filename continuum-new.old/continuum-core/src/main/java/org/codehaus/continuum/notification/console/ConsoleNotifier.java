package org.codehaus.continuum.notification.console;

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

import java.util.Map;
import java.util.Set;

import org.codehaus.continuum.notification.ContinuumNotificationDispatcher;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.notification.NotificationException;
import org.codehaus.plexus.notification.notifier.Notifier;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ConsoleNotifier.java,v 1.3 2005-03-09 20:06:43 trygvis Exp $
 */
public class ConsoleNotifier
    extends AbstractLogEnabled
    implements Notifier
{
    // ----------------------------------------------------------------------
    // Notifier Implementation
    // ----------------------------------------------------------------------

    public void sendNotification( String source, Set recipients, Map context )
        throws NotificationException
    {
        ContinuumBuild build = (ContinuumBuild) context.get( ContinuumNotificationDispatcher.CONTEXT_BUILD );

        if ( source.equals( ContinuumNotificationDispatcher.MESSAGE_ID_BUILD_STARTED ) )
        {
            buildStarted( build );
        }
        else if ( source.equals( ContinuumNotificationDispatcher.MESSAGE_ID_CHECKOUT_STARTED ) )
        {
            checkoutStarted( build );
        }
        else if ( source.equals( ContinuumNotificationDispatcher.MESSAGE_ID_CHECKOUT_COMPLETE ) )
        {
            checkoutComplete( build );
        }
        else if ( source.equals( ContinuumNotificationDispatcher.MESSAGE_ID_RUNNING_GOALS ) )
        {
            runningGoals( build );
        }
        else if ( source.equals( ContinuumNotificationDispatcher.MESSAGE_ID_GOALS_COMPLETED ) )
        {
            goalsCompleted( build );
        }
        else if ( source.equals( ContinuumNotificationDispatcher.MESSAGE_ID_BUILD_COMPLETE ) )
        {
            buildComplete( build );
        }
        else
        {
            getLogger().warn( "Unknown source: '" + source + "'." );
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private void buildStarted( ContinuumBuild build )
    {
        out( build, "Build started." );
    }

    private void checkoutStarted( ContinuumBuild build )
    {
        out( build, "Checkout started." );
    }

    private void checkoutComplete( ContinuumBuild build )
    {
        out( build, "Checkout complete." );
    }

    private void runningGoals( ContinuumBuild build )
    {
        out( build, "Running goals." );
    }

    private void goalsCompleted( ContinuumBuild build )
    {
        if ( build.getBuildResult() != null )
        {
            out( build, "Goals completed. state: " + build.getState() );
        }
        else
        {
            out( build, "Goals completed." );
        }
    }

    private void buildComplete( ContinuumBuild build )
    {
        if ( build.getBuildResult() != null )
        {
            out( build, "Build complete. state: " + build.getState() );
        }
        else
        {
            out( build, "Build complete." );
        }
    }

    private void out( ContinuumBuild build, String msg )
    {
        System.out.println( "Build event for project " + build.getProject().getName() + ":" + msg );

        System.out.println( build.getError() );
    }
}
