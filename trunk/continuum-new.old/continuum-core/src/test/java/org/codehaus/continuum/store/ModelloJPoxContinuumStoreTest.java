package org.codehaus.continuum.store;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.util.Iterator;
import java.util.Properties;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.jdo.JdoFactory;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.CollectionUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ModelloJPoxContinuumStoreTest.java,v 1.1 2005-02-28 17:04:46 trygvis Exp $
 */
public class ModelloJPoxContinuumStoreTest
    extends PlexusTestCase
{
    public void setUp()
        throws Exception
    {
        super.setUp();

        FileUtils.cleanDirectory( getTestPath( "target/plexus-home" ) );
    }

    public void testStoreProject()
        throws Exception
    {
        ContinuumStore store = (ContinuumStore) lookup( ContinuumStore.ROLE, "modello-jpox" );

        assertEquals( ModelloJPoxContinuumStore.class, store.getClass() );

        String name = "Test Project";
        String scmUrl = "scm:local:src/test/repo";
        String nagEmailAddress = "foo@bar.com";
        String version = "1.0";
        String builderId = "maven2";
        String workingDirectory = "/tmp";
        Properties configuration = new Properties();

        configuration.setProperty( "foo", "bar" );

        String projectId = store.addProject( name, scmUrl, nagEmailAddress, version, builderId, workingDirectory, configuration );

        System.err.println( "Added project, id: " + projectId );

        assertNotNull( "The project id is null.", projectId );

        System.err.println( "Loading project" );

        ContinuumProject project = store.getProject( projectId );

        System.err.println( "project.id: " + project.getId() );

        assertProjectEquals( projectId, name, scmUrl, nagEmailAddress, version, builderId, workingDirectory,
                             configuration, project );
    }

    public void testGetNonExistingProject()
        throws Exception
    {
        ContinuumStore store = (ContinuumStore) lookup( ContinuumStore.ROLE, "modello-jpox" );

        try
        {
            store.getProject( "foo" );

            fail( "Expected ContinuumStoreException.") ;
        }
        catch( ContinuumStoreException ex )
        {
            // expected
        }
    }

    public void testProjectCRUD()
        throws Exception
    {
        ContinuumStore store = (ContinuumStore) lookup( ContinuumStore.ROLE, "modello-jpox" );

        String name = "Test Project";
        String scmUrl = "scm:local:src/test/repo";
        String nagEmailAddress = "foo@bar.com";
        String version = "1.0";
        String builderId = "maven2";
        String workingDirectory = "/tmp";
        Properties properties = new Properties();

        String projectId = store.addProject( name, scmUrl, nagEmailAddress, version, builderId, workingDirectory, properties );

        assertNotNull( store.getProject( projectId ) );

        store.removeProject( projectId );

        try
        {
            store.getProject( "foo" );

            fail( "Expected ContinuumStoreException." );
        }
        catch ( ContinuumStoreException ex )
        {
            // expected
        }
    }

    public void testGetAllProjects()
        throws Exception
    {
        ContinuumStore store = (ContinuumStore) lookup( ContinuumStore.ROLE, "modello-jpox" );

        String name1 = "Test Project 1";
        String scmUrl1 = "scm:local:src/test/repo";
        String nagEmailAddress1 = "foo@bar.com";
        String version1 = "1.0";
        String builderId1 = "maven2";
        String workingDirectory1 = "/tmp";
        Properties configuration1 = new Properties();

        String id1 = store.addProject( name1, scmUrl1, nagEmailAddress1, version1, builderId1, workingDirectory1,
                                       configuration1 );

        ContinuumProject a = store.getProject( id1 );
        System.err.println( "a.name: " + a.getName() );

        String name2 = "Test Project 2";
        String scmUrl2 = "scm:local:src/test/repo";
        String nagEmailAddress2 = "foo@bar.com";
        String version2 = "1.0";
        String builderId2 = "maven2";
        String workingDirectory2 = "/tmp";
        Properties configuration2 = new Properties();

        String id2 = store.addProject( name2, scmUrl2, nagEmailAddress2, version2, builderId2, workingDirectory2,
                                       configuration2 );

        Map projects = new HashMap();

        for ( Iterator it = store.getAllProjects(); it.hasNext(); )
        {
            ContinuumProject project = (ContinuumProject) it.next();

            assertNotNull( "While getting all projects: project.id == null", project.getId() );

            assertNotNull( "While getting all projects: project.name == null", project.getName() );

            projects.put( project.getName(), project );
        }

        ContinuumProject project1 = (ContinuumProject) projects.get( name1 );

        assertProjectEquals( id1, name1, scmUrl1, nagEmailAddress1, version1, builderId1, workingDirectory1,
                             configuration1, project1 );

        ContinuumProject project2 = (ContinuumProject) projects.get( name2 );

        assertProjectEquals( id2, name2, scmUrl2, nagEmailAddress2, version2, builderId2, workingDirectory2,
                             configuration2, project2 );
    }

    public void testBuild()
        throws Exception
    {
        ContinuumStore store = (ContinuumStore) lookup( ContinuumStore.ROLE, "modello-jpox" );

        JdoFactory jdoFactory = (JdoFactory) lookup( JdoFactory.ROLE );

        jdoFactory.getPersistenceManagerFactory().close();

        String projectId = addProject( "Test Project" );

        String buildId = store.createBuild( projectId );

        Iterator it = store.getBuildsForProject( projectId, 0, 0 );

        assertNotNull( "The iterator with all builds was null.", it );

        List builds = CollectionUtils.iteratorToList( it );

        assertEquals( "Expected the build set to contain a single build.", 1, builds.size() );

        ContinuumBuild build = (ContinuumBuild) builds.get( 0 );

        assertNotNull( build );

        assertEquals( "build.id", buildId, build.getId() );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private String addProject( String name )
        throws Exception
    {
        ContinuumStore store = (ContinuumStore) lookup( ContinuumStore.ROLE, "modello-jpox" );

        String scmUrl = "scm:local:src/test/repo";
        String nagEmailAddress = "foo@bar.com";
        String version = "1.0";
        String builderId = "maven2";
        String workingDirectory = "/tmp";
        Properties configuration = new Properties();

        return store.addProject( name, scmUrl, nagEmailAddress, version, builderId, workingDirectory, configuration );
    }

    private void assertProjectEquals( String projectId, String name, String scmUrl, String nagEmailAddress, String version, String builderId, String workingDirectory, Properties configuration, ContinuumProject project )
    {
        assertEquals( "project.id", projectId, project.getId() );

        assertEquals( "porject.name", name, project.getName() );

        assertEquals( "porject.scmUrl", scmUrl, project.getScmUrl() );

        assertEquals( "project.nagEmailAddress", nagEmailAddress, project.getNagEmailAddress() );

        assertEquals( "project.version", version, project.getVersion() );

        assertEquals( "project.builderId", builderId, project.getBuilderId() );

        assertEquals( "project.workingDirectory", workingDirectory, project.getWorkingDirectory() );

        for ( Iterator it = configuration.keySet().iterator(); it.hasNext(); )
        {
            String key = (String) it.next();

            String value = project.getConfiguration().getProperty( key );

            assertNotNull( "Value for key '" + key + "' was null.", value );

            assertEquals( "The values for '" + key + "' doesn't match.", configuration.getProperty( key ), value );
        }
    }
}
