package org.codehaus.continuum;

/*
 * LICENSE
 */

import java.io.File;

import org.codehaus.continuum.builder.BuilderManager;
import org.codehaus.continuum.builder.ContinuumBuilder;
import org.codehaus.continuum.buildqueue.BuildQueue;
import org.codehaus.continuum.notification.NotifierManager;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumBuildResult;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ContinuumProjectState;
import org.codehaus.continuum.scm.ContinuumScm;
import org.codehaus.continuum.store.ContinuumStore;
import org.codehaus.continuum.store.ContinuumStoreException;
import org.codehaus.plexus.logging.Logger;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l </a>
 * @version $Id: BuilderThread.java,v 1.4 2004-07-27 00:06:03 trygvis Exp $
 */
class BuilderThread
    implements Runnable
{
    /** */
    private BuilderManager builderManager;

    /** */
    private BuildQueue buildQueue;

    /** */
    private ContinuumStore store;

    /** */
    private NotifierManager notifier;

    /** */
    private ContinuumScm scm;

    /** */
    private Logger logger;

    /** */
    private boolean shutdown;

    /** */
    private boolean done;

    public BuilderThread( BuilderManager builderManager, BuildQueue buildQueue, ContinuumStore store,
        NotifierManager notifier, ContinuumScm scm, Logger logger )
    {
        this.builderManager = builderManager;

        this.buildQueue = buildQueue;

        this.store = store;

        this.notifier = notifier;

        this.scm = scm;

        this.logger = logger;
    }

    public void run()
    {
        while ( !shutdown )
        {
            String buildId = buildQueue.dequeue();

            if ( buildId == null )
            {
                sleep( 100 );

                continue;
            }

            try
            {
                store.beginTransaction();

                store.setBuildResult( buildId, ContinuumProjectState.BUILDING, null, null );

                store.commitTransaction();
            }
            catch( ContinuumStoreException ex )
            {
                getLogger().error( "Exception while setting state flag.", ex );

                rollback();

                continue;
            }

            try
            {
                store.beginTransaction();

                buildProject( buildId );

                store.commitTransaction();
            }
            catch( Exception ex )
            {
                getLogger().error( "Internal error while building project.", ex );

                rollback();

                continue;
            }
        }

        done = true;
    }

    public void shutdown()
    {
        shutdown = true;
    }

    public boolean isDone()
    {
        return done;
    }

    private Logger getLogger()
    {
        return logger;
    }

    private void sleep( int interval )
    {
        try
        {
            Thread.sleep( interval );
        }
        catch ( InterruptedException ex )
        {
            // ignore
        }
    }

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

            getLogger().fatalError( "Error building project.", ex );

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

        File workingDirectory;

        try
        {
            notifier.checkoutStarted( build );

            workingDirectory = checkOut( project );
        }
        finally
        {
            notifier.checkoutComplete( build );
        }

        notifier.runningGoals( build );

        ContinuumBuildResult result;

        try
        {
            result = runGoals( builder, workingDirectory, build );
        }
        finally
        {
            notifier.goalsCompleted( build );
        }

        return result;
    }

    private File checkOut( ContinuumProject project )
        throws ContinuumException
    {
        scm.clean( project );

        File workingDirectory = scm.checkout( project );

        return workingDirectory;
    }

    private ContinuumBuildResult runGoals( ContinuumBuilder builder, File workingDirectory, ContinuumBuild build )
        throws ContinuumException
    {
        ContinuumBuildResult result = builder.build( workingDirectory, build );

        if ( result == null )
        {
            getLogger().fatalError( "Internal error: the builder returned null." );
        }

        return result;
    }

    // ----------------------------------------------------------------------
    // Database helpers
    // ----------------------------------------------------------------------

    private void rollback()
    {
        try
        {
            store.rollbackTransaction();
        }
        catch( Exception ex )
        {
            getLogger().error( "Exception while rolling back transaction, ignored.", ex );
        }
    }
}
