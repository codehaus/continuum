package org.codehaus.continuum.store;

/*
 * LICENSE
 */

import java.util.Iterator;

import org.codehaus.continuum.project.BuildResult;
import org.codehaus.continuum.project.ContinuumProject;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumStore.java,v 1.7 2004-07-03 03:21:16 trygvis Exp $
 */
public interface ContinuumStore
{
    String ROLE = ContinuumStore.class.getName();

    // ----------------------------------------------------------------------
    // ContinuumProject
    // ----------------------------------------------------------------------

    String addProject( String name, String scmConnection )
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

    void setBuildResult( String buildId, int state, Throwable error )
        throws ContinuumStoreException;

    BuildResult getBuildResult( String buildId )
        throws ContinuumStoreException;
}
