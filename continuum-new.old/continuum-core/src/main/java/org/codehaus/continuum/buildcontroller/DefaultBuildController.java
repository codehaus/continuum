package org.codehaus.continuum.buildcontroller;

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

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.builder.ContinuumBuilder;
import org.codehaus.continuum.builder.manager.BuilderManager;
import org.codehaus.continuum.notification.ContinuumNotificationDispatcher;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumBuildResult;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ContinuumProjectState;
import org.codehaus.continuum.scm.ContinuumScm;
import org.codehaus.continuum.store.ContinuumStore;
import org.codehaus.continuum.store.ContinuumStoreException;
import org.codehaus.plexus.logging.AbstractLogEnabled;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultBuildController.java,v 1.6 2005-03-10 00:05:30 trygvis Exp $
 */
public class DefaultBuildController
    extends AbstractLogEnabled
    implements BuildController
{
    /** @requirement */
    private BuilderManager builderManager;

    /** @requirement */
    private ContinuumStore store;

    /** @requirement */
    private ContinuumNotificationDispatcher notifier;

    /** @requirement */
    private ContinuumScm scm;

    // ----------------------------------------------------------------------
    // BuildController Implementation
    // ----------------------------------------------------------------------

    public void build( String buildId )
    {
        try
        {
            store.setBuildResult( buildId, ContinuumProjectState.BUILDING, null, null );
        }
        catch ( ContinuumStoreException ex )
        {
            getLogger().error( "Exception while setting the state flag.", ex );

            return;
        }

        try
        {
            buildProject( buildId );
        }
        catch ( ContinuumStoreException ex )
        {
            getLogger().error( "Internal error while building the project.", ex );

            return;
        }
        catch ( ContinuumException ex )
        {
            if ( !Thread.interrupted() )
            {
                getLogger().error( "Internal error while building the project.", ex );
            }

            return;
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    /**
     * This method shall not throw any exceptions unless there
     * is something internally wrong. It shall NOT throw a exception when a build fails.
     *
     * @param buildId
     * @throws ContinuumException
     * @throws ContinuumStoreException
     */
    private void buildProject( String buildId )
        throws ContinuumException, ContinuumStoreException
    {
        // if these calls fail we're screwed anyway
        // and it will only be logged through the logger.

        ContinuumBuild build = store.getBuild( buildId );

        ContinuumBuilder builder = builderManager.getBuilder( build.getProject().getBuilderId() );

        try
        {
            notifier.buildStarted( build );

            ContinuumBuildResult result = build( builder, build );

            int state;

            if ( result.isSuccess() )
            {
                state = ContinuumProjectState.OK;
            }
            else
            {
                state = ContinuumProjectState.FAILED;
            }

            store.setBuildResult( buildId, state, result, null );
        }
        catch ( Throwable ex )
        {
            store.setBuildResult( buildId, ContinuumProjectState.ERROR, null, ex );

            getLogger().fatalError( "Error building the project, build id: '" + buildId + "'.", ex );
        }
        finally
        {
            notifier.buildComplete( build );
        }
    }

    private ContinuumBuildResult build( ContinuumBuilder builder, ContinuumBuild build )
        throws Exception
    {
        ContinuumProject project = build.getProject();

        try
        {
            notifier.checkoutStarted( build );

            scm.updateProject( project );
        }
        finally
        {
            notifier.checkoutComplete( build );
        }

        notifier.runningGoals( build );

        ContinuumBuildResult result;

        try
        {
            result = runGoals( builder, project );
        }
        finally
        {
            notifier.goalsCompleted( build );
        }

        return result;
    }

    private ContinuumBuildResult runGoals( ContinuumBuilder builder, ContinuumProject project )
        throws ContinuumException
    {
        ContinuumBuildResult result = builder.build( project );

        if ( result == null )
        {
            getLogger().fatalError( "Internal error: the builder returned null." );
        }

        return result;
    }
}
