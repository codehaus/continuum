package org.codehaus.continuum.projectstorage;

/*
 * LICENSE
 */

import java.util.Map;

import org.apache.maven.project.MavenProject;

import org.codehaus.continuum.AbstractContinuumTest;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AbstractProjectStorageTest.java,v 1.6 2004-07-02 03:27:07 trygvis Exp $
 */
public abstract class AbstractProjectStorageTest
    extends AbstractContinuumTest
{
    public void testBasic()
        throws Exception
    {/*
        Iterator i;
        Map projects = new HashMap();
        ContinuumStore store;
        MavenProject p;

        store = (ContinuumStore) lookup( ContinuumStore.class.getName() );

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
*/
/*
        ContinuumProject project = new ContinuumProject();

        MavenProjectBuilder mavenProjectBuidler = (MavenProjectBuilder) lookup( MavenProjectBuilder.ROLE );

        MavenProject mavenProject = mavenProjectBuilder.build( "" );

        project.setMavenProject( mavenProject );

        project.setProjectState( ContinuumProject.PROJECT_STATE_OK );

        store.storeProject( project );
*//*
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
*/
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
