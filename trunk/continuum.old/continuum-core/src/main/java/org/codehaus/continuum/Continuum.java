package org.codehaus.continuum;

import java.util.List;

public interface Continuum
{
    String ROLE = Continuum.class.getName();
/*
    void addProject( MavenProject project )
        throws ContinuumException;
*/
    /**
     * Adds the project. Continuum will download the project and
     * persist it.
     *
     * @param url
     * @throws ContinuumException
     */
    void addProject( String url )
        throws ContinuumException;

    /**
     * Signals continuum to build a specific project.
     *
     * @param groupId The group id of the project.
     * @param artifactId The artifact id of the project.
     * @return Returns the id of the build job.
     * @throws ContinuumException
     */
    String buildProject( String groupId, String artifactId )
        throws ContinuumException;

    /**
     * Signals continuum to build all projects.
     * 
     * TODO: this should probably be removed from the interface.
     * 
     * @return Returns a list of ids of the build jobs.
     * @throws ContinuumException
     */
    List buildProjects()
        throws ContinuumException;

    /**
     * Returns the current state of continuum. 
     * 
     * If this continuum is a frontend for other continuum server or other
     * dependent services it will report busy if any of the services are busy.
     *
     * @return Returns the current state of continuum.
     */
    int getState()
        throws ContinuumException;

    /**
     * Returns the current length of the build queue.
     * 
     * @return Returns the current length of the build queue.
     */
    int getBuildQueueLength()
        throws ContinuumException;
}
