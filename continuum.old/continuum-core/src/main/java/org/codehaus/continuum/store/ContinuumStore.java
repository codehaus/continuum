package org.codehaus.continuum.store;

/*
 * LICENSE
 */

import java.util.Iterator;

import org.apache.maven.ExecutionResponse;

import org.codehaus.continuum.project.BuildResult;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ProjectDescriptor;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumStore.java,v 1.10 2004-07-19 16:28:17 trygvis Exp $
 */
public interface ContinuumStore
{
    String ROLE = ContinuumStore.class.getName();

    // ----------------------------------------------------------------------
    // Transaction handling
    // ----------------------------------------------------------------------

    public void beginTransaction()
        throws ContinuumStoreException;

    public void commitTransaction()
        throws ContinuumStoreException;

    public void rollbackTransaction()
        throws ContinuumStoreException;

    // ----------------------------------------------------------------------
    // ContinuumProject
    // ----------------------------------------------------------------------

    String addProject( String name, String scmConnection, String type )
        throws ContinuumStoreException;

    void setProjectDescriptor( String projectId, ProjectDescriptor descriptor )
        throws ContinuumStoreException;

    Iterator getAllProjects()
        throws ContinuumStoreException;

    ContinuumProject getProject( String projectId )
        throws ContinuumStoreException;

    // ----------------------------------------------------------------------
    // Build
    // ----------------------------------------------------------------------

    String createBuildResult( String projectId )
        throws ContinuumStoreException;

    void setBuildResult( String buildId, int state, Throwable error, ExecutionResponse executionResponse )
        throws ContinuumStoreException;

    BuildResult getBuildResult( String buildId )
        throws ContinuumStoreException;

    Iterator getBuildResultsForProject( String projectId, int start, int end )
        throws ContinuumStoreException;
}
