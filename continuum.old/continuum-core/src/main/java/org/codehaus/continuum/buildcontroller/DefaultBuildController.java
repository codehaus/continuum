package org.codehaus.continuum.buildcontroller;

/*
 * LICENSE
 */

import java.io.File;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.builder.BuilderManager;
import org.codehaus.continuum.builder.ContinuumBuilder;
import org.codehaus.continuum.notification.NotifierManager;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumBuildResult;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ContinuumProjectState;
import org.codehaus.continuum.scm.ContinuumScm;
import org.codehaus.continuum.scm.ContinuumScmException;
import org.codehaus.continuum.store.ContinuumStore;
import org.codehaus.continuum.store.ContinuumStoreException;
import org.codehaus.continuum.store.tx.StoreTransactionManager;
import org.codehaus.plexus.logging.AbstractLogEnabled;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultBuildController.java,v 1.3 2004-10-29 15:18:12 trygvis Exp $
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
    private StoreTransactionManager txManager;

    /** @requirement */
    private NotifierManager notifier;

    /** @requirement */
    private ContinuumScm scm;

    // ----------------------------------------------------------------------
    // BuildController Implementation
    // ----------------------------------------------------------------------

    public void build( String buildId )
    {
        try
        {
            txManager.begin();

            store.setBuildResult( buildId, ContinuumProjectState.BUILDING, null, null );

            txManager.commit();
        }
        catch( ContinuumStoreException ex )
        {
            getLogger().error( "Exception while setting the state flag.", ex );

            txManager.rollback();

            return;
        }

        try
        {
            txManager.begin();

            buildProject( buildId );

            txManager.commit();

            if ( false )
            {
                throw new InterruptedException();
            }
        }
        catch( InterruptedException ex )
        {
            txManager.rollback();

            return;
        }
        catch( ContinuumStoreException ex )
        {
            getLogger().error( "Internal error while building the project.", ex );

            txManager.rollback();

            return;
        }
        catch( ContinuumException ex )
        {
            if ( !Thread.interrupted() )
            {
                getLogger().error( "Internal error while building the project.", ex );
            }

            txManager.rollback();

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

        ContinuumBuilder builder = builderManager.getBuilderForBuild( buildId );

        ContinuumBuild build = store.getBuild( buildId );

        try
        {
            notifier.buildStarted( build );

            ContinuumBuildResult result = build( builder, build );

            ContinuumProjectState state;

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

            getLogger().fatalError( "Error building the project.", ex );

            return;
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

            update( project );
        }
        finally
        {
            notifier.checkoutComplete( build );
        }

        notifier.runningGoals( build );

        ContinuumBuildResult result;

        try
        {
            result = runGoals( builder, project.getWorkingDirectory(), build );
        }
        finally
        {
            notifier.goalsCompleted( build );
        }

        return result;
    }

    private void update( ContinuumProject project )
        throws ContinuumScmException
    {
        scm.updateProject( project );
    }

    private ContinuumBuildResult runGoals( ContinuumBuilder builder, String workingDirectory, ContinuumBuild build )
        throws ContinuumException
    {
        ContinuumBuildResult result = builder.build( new File( workingDirectory ), build );

        if ( result == null )
        {
            getLogger().fatalError( "Internal error: the builder returned null." );
        }

        return result;
    }
}
