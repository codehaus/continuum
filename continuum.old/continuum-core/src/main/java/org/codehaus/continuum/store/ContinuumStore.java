package org.codehaus.continuum.projectstorage;

/*
 * LICENSE
 */

import java.util.Iterator;

import org.codehaus.continuum.project.ContinuumProject;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumStore.java,v 1.4 2004-06-27 22:20:27 trygvis Exp $
 */
public interface ProjectStorage {
    String storeProject( ContinuumProject project )
        throws ProjectStorageException;

    Iterator getAllProjects()
        throws ProjectStorageException;

    ContinuumProject getProject( String projectId )
        throws ProjectStorageException;
}
