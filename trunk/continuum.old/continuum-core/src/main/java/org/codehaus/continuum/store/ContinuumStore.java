package org.codehaus.plexus.continuum.projectstorage;

/*
 * LICENSE
 */

import java.io.Reader;
import java.util.Iterator;

import org.apache.maven.project.MavenProject;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumStore.java,v 1.2 2004-04-22 20:03:41 trygvis Exp $
 */
public interface ProjectStorage {
    void storeProject( String groupId, String artifactId, Reader input )
        throws ProjectStorageException;

    Iterator getAllProjects()
        throws ProjectStorageException;

    MavenProject getProject( String groupId, String artifactId )
        throws ProjectStorageException;
}
