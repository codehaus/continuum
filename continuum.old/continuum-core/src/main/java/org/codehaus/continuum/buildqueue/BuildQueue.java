package org.codehaus.plexus.continuum.buildqueue;

/*
 * LICENSE
 */

import org.apache.maven.project.MavenProject;

import org.codehaus.plexus.continuum.ContinuumException;

/**
 * A queue of <code>MavenProjectBuild</code>'s.
 *
 * A <code>BuildQueue</code> implementation MUST be thread safe.
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: BuildQueue.java,v 1.1 2004-04-24 23:54:13 trygvis Exp $
 */
public interface BuildQueue
{
    public final static String ROLE = BuildQueue.class.getName();

    /**
     * Returns a element from the queue. 
     * 
     * Returns <code>null</code> if the queue is empty.
     * 
     * @return Returns a element from the queue.
     * @throws ContinuumException
     */
    MavenProject dequeue()
        throws ContinuumException;

    /**
     * Adds a project to the queue.
     * 
     * @param groupId The group id.
     * @param artifactId The artifact id.
     * @return Returns the build id.
     * @throws ContinuumException
     */
    String enqueue( MavenProject project )
        throws ContinuumException;

    /**
     * Returns the length of the queue.
     * 
     * @return Returns the length of the queue.
     * @throws ContinuumException
     */
    int getLength()
        throws ContinuumException;
}
