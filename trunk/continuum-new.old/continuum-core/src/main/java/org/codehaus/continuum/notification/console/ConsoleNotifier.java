package org.codehaus.continuum.notification.console;

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

import java.util.Map;
import java.util.Set;

import org.codehaus.continuum.notification.ContinuumNotificationDispatcher;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.notification.NotificationException;
import org.codehaus.plexus.notification.notifier.Notifier;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ConsoleNotifier.java,v 1.4 2005-03-10 00:05:51 trygvis Exp $
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
