package org.codehaus.continuum.buildqueue;

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

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.taskqueue.TaskQueue;
import org.codehaus.plexus.taskqueue.TaskQueueException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultBuildQueue.java,v 1.1 2005-02-22 10:12:18 trygvis Exp $
 */
public class DefaultBuildQueue
    extends AbstractBuildQueue
    implements BuildQueue
{
    /** @requirement */
    private TaskQueue taskQueue;

    // ----------------------------------------------------------------------
    // BuildQueue Implementation
    // ----------------------------------------------------------------------

    public String dequeue()
        throws BuildQueueException
    {
        try
        {
            BuildProjectTask task = (BuildProjectTask) taskQueue.take();

            if ( task == null )
            {
                return null;
            }

            return task.getBuildId();
        }
        catch ( TaskQueueException e )
        {
            throw new BuildQueueException( "Error while getting a task from the task queue.", e );
        }
    }

    public void enqueue( String projectId, String buildId )
        throws BuildQueueException
    {
        BuildProjectTask task = new BuildProjectTask( projectId, buildId, System.currentTimeMillis() );

        try
        {
            taskQueue.put( task );
        }
        catch ( TaskQueueException e )
        {
            throw new BuildQueueException( "Error while enqueing project.", e );
        }
    }
}
