package org.codehaus.continuum;

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

import org.codehaus.continuum.buildcontroller.BuildController;
import org.codehaus.continuum.buildqueue.BuildQueue;
import org.codehaus.continuum.buildqueue.BuildQueueException;
import org.codehaus.plexus.logging.Logger;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l </a>
 * @version $Id: BuilderThread.java,v 1.2 2005-02-22 10:12:18 trygvis Exp $
 */
public class BuilderThread
    implements Runnable
{
    /** */
    private BuildController buildController;

    /** */
    private BuildQueue buildQueue;

    /** */
    private Logger logger;

    /** */
    private boolean shutdown;

    /** */
    private boolean done;

    public BuilderThread( BuildController buildController, BuildQueue buildQueue, Logger logger )
    {
        this.buildController = buildController;

        this.buildQueue = buildQueue;

        this.logger = logger;
    }

    public void run()
    {
        while ( !shutdown )
        {
            String buildId = null;

            buildId = dequeue();

            if ( buildId == null )
            {
                sleep( 100 );

                continue;
            }

            buildController.build( buildId );
        }

        getLogger().info( "Builder thread exited." );

        done = true;

        synchronized ( this )
        {
            notifyAll();
        }
    }

    public void shutdown()
    {
        getLogger().info( "Builder thread got shutdown signal." );

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

    private String dequeue()
    {
        try
        {
            return buildQueue.dequeue();
        }
        catch ( BuildQueueException e )
        {
            getLogger().warn( "Error while getting build from the queue.", e );

            // TODO: Sleep for 10 seconds to give the system some time to breath.
            // If interruped it will return properly.
            sleep( 10 * 1000 );

            return null;
        }
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
