package org.codehaus.continuum.store;

/*
 * LICENSE
 */

import java.util.Iterator;

import org.codehaus.continuum.project.BuildResult;
import org.codehaus.continuum.project.BuildResultState;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ProjectDescriptor;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumStore.java,v 1.1 2004-07-20 18:26:16 trygvis Exp $
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

    void setBuildResult( String buildId, BuildResultState state, Throwable error, Object builderBuildResult )
        throws ContinuumStoreException;

    BuildResult getBuildResult( String buildId )
        throws ContinuumStoreException;

    Iterator getBuildResultsForProject( String projectId, int start, int end )
        throws ContinuumStoreException;
}
