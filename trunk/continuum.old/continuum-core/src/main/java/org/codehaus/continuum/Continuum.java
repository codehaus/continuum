package org.codehaus.continuum;

import org.apache.maven.project.MavenProject;

public interface Continuum
{
    String ROLE = Continuum.class.getName();

    String addProject( MavenProject project )
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
