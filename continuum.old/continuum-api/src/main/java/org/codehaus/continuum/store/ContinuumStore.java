package org.codehaus.continuum.store;

/*
 * LICENSE
 */

import java.util.Iterator;

import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumBuildResult;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ContinuumProjectState;
import org.codehaus.continuum.project.ProjectDescriptor;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumStore.java,v 1.2 2004-07-27 00:06:03 trygvis Exp $
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

    void updateProject( String projectId, String name, String scmUrl )
        throws ContinuumStoreException;

    Iterator getAllProjects()
        throws ContinuumStoreException;

    ContinuumProject getProject( String projectId )
        throws ContinuumStoreException;

    // ----------------------------------------------------------------------
    // Build
    // ----------------------------------------------------------------------

    String createBuild( String projectId )
        throws ContinuumStoreException;

    void setBuildResult( String buildId, ContinuumProjectState state, ContinuumBuildResult result, Throwable error )
        throws ContinuumStoreException;

    ContinuumBuild getBuild( String buildId )
        throws ContinuumStoreException;

    Iterator getBuildsForProject( String projectId, int start, int end )
        throws ContinuumStoreException;
}
