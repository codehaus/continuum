package org.codehaus.continuum;

/*
 * LICENSE
 */

import org.codehaus.continuum.builder.BuilderManager;
import org.codehaus.continuum.builder.ContinuumBuilder;
import org.codehaus.continuum.buildqueue.BuildQueue;
import org.codehaus.plexus.logging.Logger;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l </a>
 * @version $Id: BuilderThread.java,v 1.3 2004-07-07 02:34:33 trygvis Exp $
 */
class BuilderThread
    implements Runnable
{
    /** */
    private BuilderManager builderManager;

    /** */
    private BuildQueue buildQueue;

    /** */
    private Logger logger;

    /** */
    private boolean shutdown;

    /** */
    private boolean done;

    public BuilderThread( BuilderManager builderManager, BuildQueue buildQueue, Logger logger )
    {
        this.builderManager = builderManager;

        this.buildQueue = buildQueue;

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
                ContinuumBuilder builder = builderManager.getBuilderForBuild( buildId );

                builder.build( buildId );
            }
            catch( ContinuumException ex )
            {
                getLogger().error( "Internal error while building project.", ex );
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
}
