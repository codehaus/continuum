package org.codehaus.continuum;

/*
 * LICENSE
 */

import org.codehaus.continuum.builder.ContinuumBuilder;
import org.codehaus.continuum.buildqueue.BuildQueue;
import org.codehaus.plexus.logging.Logger;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l </a>
 * @version $Id: BuilderThread.java,v 1.2 2004-07-03 03:21:13 trygvis Exp $
 */
class BuilderThread
    implements Runnable
{
    /** */
    private BuildQueue buildQueue;

    /** */
    private Logger logger;

    /** */
    private ContinuumBuilder builder;

    /** */
    private boolean shutdown;

    /** */
    private boolean done;

    public BuilderThread( BuildQueue buildQueue, Logger logger, ContinuumBuilder builder )
    {
        this.buildQueue = buildQueue;

        this.logger = logger;

        this.builder = builder;
    }

    public void run()
    {
        while ( !shutdown )
        {
            String buildId = buildQueue.dequeue();

            if ( buildId == null )
            {
//                logger.info( "Builder sleeping..." );

                sleep( 100 );

                continue;
            }

            builder.build( buildId );
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