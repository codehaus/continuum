package org.codehaus.continuum.buildqueue;

/*
 * LICENSE
 */

import java.util.LinkedList;
import java.util.List;

import org.codehaus.continuum.ContinuumException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: SimpleBuildQueue.java,v 1.3 2004-07-01 15:30:57 trygvis Exp $
 */
public class SimpleBuildQueue extends AbstractBuildQueue
{
    /**
     * The queue of elements.
     */
    List queue = new LinkedList();

    public String dequeue()
    {
        synchronized( queue )
        {
            if ( queue.size() == 0 )
                return null;

            return (String)queue.remove( 0 );
        }
    }

    public void enqueue( String buildId )
    {
        synchronized( queue )
        {
            queue.add( buildId );
        }
    }

    /**
     * Returns the length of the queue.
     * 
     * @return Returns the length of the queue.
     * @throws ContinuumException
     */
    public int getLength()
    {
        synchronized( queue )
        {
            return queue.size();
        }
    }
}
