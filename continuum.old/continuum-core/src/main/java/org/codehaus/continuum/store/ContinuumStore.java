package org.codehaus.plexus.continuum.projectstorage;

/*
 * LISENCE
 */

import java.io.Reader;
import java.util.Iterator;

import org.apache.maven.project.MavenProject;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumStore.java,v 1.1 2004-04-07 15:56:56 trygvis Exp $
 */
public interface ProjectStorage {
    void storeProject( String groupId, String artifactId, Reader input )
        throws ProjectStorageException;

    Iterator getAllProjects()
        throws ProjectStorageException;

    MavenProject getProject( String groupId, String artifactId )
        throws ProjectStorageException;
}
