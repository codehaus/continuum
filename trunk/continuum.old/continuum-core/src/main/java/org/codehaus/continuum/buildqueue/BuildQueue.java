package org.codehaus.continuum.buildqueue;

/*
 * LICENSE
 */

import org.codehaus.continuum.ContinuumException;

/**
 * A queue of build job ids.
 *
 * A <code>BuildQueue</code> implementation MUST be thread safe.
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: BuildQueue.java,v 1.3 2004-07-01 15:30:57 trygvis Exp $
 */
public interface BuildQueue
{
    public final static String ROLE = BuildQueue.class.getName();

    /**
     * Returns a element from the queue. 
     * 
     * Returns <code>null</code> if the queue is empty.
     * 
     * @return Returns a element from the queue or <code>null</code> if the queue is empty.
     */
    String dequeue();

    /**
     * Adds a build id to the queue.
     * 
     * @param groupId The group id.
     * @param artifactId The artifact id.
     * @return Returns the build id.
     * @throws ContinuumException
     */
    void enqueue( String project );

    /**
     * Returns the length of the queue.
     * 
     * @return Returns the length of the queue.
     * @throws ContinuumException
     */
    int getLength();
}
