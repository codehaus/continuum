package org.codehaus.plexus.continuum.buildqueue;

/*
 * LICENSE
 */

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.project.MavenProject;

import org.codehaus.plexus.continuum.ContinuumException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: SimpleBuildQueue.java,v 1.1 2004-04-24 23:54:13 trygvis Exp $
 */
public class SimpleBuildQueue extends AbstractBuildQueue
{
    /**
     * The queue of elements.
     */
    List queue = new LinkedList();

    /**
     * The build id.
     */
    BigInteger nextId = BigInteger.ZERO;

    public MavenProject dequeue()
    {
        synchronized( queue )
        {
            if ( queue.size() == 0 )
                return null;

            return (MavenProject)queue.remove( 0 );
        }
    }

    public String enqueue( MavenProject project )
    {
        synchronized( queue )
        {
            BigInteger id = nextId;

            nextId = nextId.add( BigInteger.ONE );

            queue.add( project );

            return id.toString();
        }
    }


    /**
     * Returns the length of the queue.
     * 
     * @return Returns the length of the queue.
     * @throws ContinuumException
     */
    public int getLength()
        throws ContinuumException
    {
        synchronized( queue )
        {
            return queue.size();
        }
    }
}
