package org.codehaus.continuum.projectstorage;

/*
 * LICENSE
 */

import java.util.Iterator;

import org.apache.maven.project.MavenProject;

import org.codehaus.continuum.project.ContinuumProject;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumStore.java,v 1.5 2004-06-27 23:21:03 trygvis Exp $
 */
public interface ContinuumProjectStorage {

    // ----------------------------------------------------------------------
    // ContinuumProject
    // ----------------------------------------------------------------------

    String storeProject( MavenProject project )
        throws ContinuumProjectStorageException;

    Iterator getAllProjects()
        throws ContinuumProjectStorageException;

    ContinuumProject getProject( String projectId )
        throws ContinuumProjectStorageException;

    // ----------------------------------------------------------------------
    // Build
    // ----------------------------------------------------------------------

    String createBuild( ContinuumProject project )
        throws ContinuumProjectStorageException;
}
