package org.codehaus.continuum;

import java.util.Iterator;

public interface Continuum
{
    String ROLE = Continuum.class.getName();

    String addProject( String name, String scmConnection, String type )
        throws ContinuumException;

    Iterator getAllProjects( int start, int end )
        throws ContinuumException;

    /**
     * Signals continuum to build a specific project.
     *
     * @param id The project id
     * @return Returns the build id of the job.
     * @throws ContinuumException
     */
    String buildProject( String id )
        throws ContinuumException;

    /**
     * Returns the current length of the build queue.
     * 
     * @return Returns the current length of the build queue.
     */
    int getBuildQueueLength()
        throws ContinuumException;
}
