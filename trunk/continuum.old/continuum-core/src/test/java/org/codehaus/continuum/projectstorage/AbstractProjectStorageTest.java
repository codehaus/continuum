package org.codehaus.continuum.projectstorage;

/*
 * LICENSE
 */

import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.maven.project.MavenProject;

import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AbstractProjectStorageTest.java,v 1.3 2004-05-13 17:48:17 trygvis Exp $
 */
public abstract class AbstractProjectStorageTest
    extends PlexusTestCase
{
    public void testBasic()
        throws Exception
    {
        Iterator i;
        Map projects = new HashMap();
        ProjectStorage store;
        MavenProject p;

        store = (ProjectStorage)lookup( ProjectStorage.class.getName() );

        i = store.getAllProjects();

        while ( i.hasNext() )
        {
            p = (MavenProject)i.next();

            projects.put(p.getGroupId() + "/" + p.getArtifactId(), p);
        }

        assertHasProject( projects, "plexus", "plexus-bayesian" );
        assertHasProject( projects, "plexus", "plexus-i18n" );
        assertHasProject( projects, "inamo", "accountmanager" );

        assertEquals( 0, projects.size() );

        store.storeProject( "maven","maven-ajc-plugin", new FileReader( getTestFile( "src/test-input/project.xml" ) ) );

        i = store.getAllProjects();

        while ( i.hasNext() )
        {
            p = (MavenProject)i.next();

            projects.put(p.getGroupId() + "/" + p.getArtifactId(), p);
        }

        assertHasProject( projects, "plexus", "plexus-bayesian" );
        assertHasProject( projects, "plexus", "plexus-i18n" );
        assertHasProject( projects, "inamo", "accountmanager" );
        assertHasProject( projects, "maven", "maven-ajc-plugin" );

        assertEquals( 0, projects.size() );
    }

    private void assertHasProject( Map projects, String groupId, String artifactId )
    {
        MavenProject p;

        p = (MavenProject)projects.remove( groupId + "/" + artifactId );

        assertNotNull( "Missing project " + groupId + "/" + artifactId, p );

        assertEquals( groupId, p.getGroupId() );
        assertEquals( artifactId, p.getArtifactId() );
    }
}
