package org.codehaus.continuum.store;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.continuum.AbstractContinuumTest;
import org.codehaus.continuum.builder.test.TestBuildResult;
import org.codehaus.continuum.builder.test.TestProjectDescriptor;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumBuildResult;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ContinuumProjectState;
import org.codehaus.continuum.project.ProjectDescriptor;
import org.codehaus.continuum.store.tx.StoreTransactionManager;
import org.codehaus.plexus.util.CollectionUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AbstractContinuumStoreTest.java,v 1.8 2004-10-24 20:39:11 trygvis Exp $
 */
public abstract class AbstractContinuumStoreTest
    extends AbstractContinuumTest
{
    // TODO: Add tests for the ordering of builds

    private ContinuumStore store;

    private StoreTransactionManager txManager;

    protected abstract String getRoleHint();

    public void setUp()
        throws Exception
    {
        super.setUp();

        store = (ContinuumStore) lookup( ContinuumStore.ROLE, getRoleHint() );

        txManager = (StoreTransactionManager) lookup( StoreTransactionManager.ROLE, getRoleHint() );
    }

    // ----------------------------------------------------------------------
    // Project tests
    // ----------------------------------------------------------------------

    public void testContinuumProject()
        throws Exception
    {
        String projectName = "Foo Project";

        String scmUrl = "scm:cvs:local:ignored:/cvsroot:module";

        String nagEmailAddress = "foo@bar";

        String version = "1";

        String type = "mock";

        // --- -- -

        txManager.begin();

        String id = store.addProject( projectName, scmUrl, nagEmailAddress, version, type );

        assertNotNull( id );

        txManager.commit();

        // --- -- -

        txManager.begin();

        ContinuumProject project = store.getProject( id );

        // Check a 'blank' project

        assertNotNull( project );

        assertEquals( id, project.getId() );

        assertEquals( projectName, project.getName() );

        assertEquals( scmUrl, project.getScmUrl() );

        assertEquals( type, project.getType() );

        assertEquals( ContinuumProjectState.NEW, project.getState() );

        assertNull( project.getDescriptor() );

        txManager.commit();

        // --- -- -
        // Check the descriptor

        txManager.begin();

        project = store.getProject( id );

        TestProjectDescriptor descriptor = new TestProjectDescriptor();

        descriptor.setProjectId( id );

        descriptor.setProject( project );

        descriptor.setAttribute( 666 );

        store.setProjectDescriptor( id, descriptor );

        txManager.commit();

        // --- -- -

        txManager.begin();

        project = store.getProject( id );

        assertNotNull( project );

//        assertEquals( type, project.getType() );

        ProjectDescriptor desc = project.getDescriptor();

        assertNotNull( desc );

        assertEquals( TestProjectDescriptor.class.getName(), desc.getClass().getName() );

        descriptor = (TestProjectDescriptor)desc;

        assertEquals( 666, descriptor.getAttribute() );

        txManager.commit();
    }

    public void testFindProjectsByName()
        throws Exception
    {
        txManager.begin();

        store.addProject( "aaa", "scm:test:foo", "foo@bar", "1.0", "test" );

        store.addProject( "aab", "scm:test:foo", "foo@bar", "1.0", "test" );

        store.addProject( "baa", "scm:test:foo", "foo@bar", "1.0", "test" );

        txManager.commit();

        txManager.begin();

        Iterator it = store.findProjectsByName( "aaa" );

        assertNotNull( it );

        assertTrue( it.hasNext() );

        ContinuumProject project = (ContinuumProject) it.next();

        assertEquals( "aaa", project.getName() );

        assertFalse( it.hasNext() );

        txManager.commit();
    }

    public void testRemoveProject()
        throws Exception
    {
        txManager.begin();

        String projectId1 = store.addProject( "p1", "scm:test:", "foo@bar", "1.0", "test" );

        String projectId2 = store.addProject( "p2", "scm:test:", "foo@bar", "1.0", "test" );

//        ContinuumProject project1

        String buildId1 = store.createBuild( projectId1 );

        String buildId2 = store.createBuild( projectId2 );

        ContinuumBuild build1 = store.getBuild( buildId1 );

        ContinuumBuildResult result = new TestBuildResult( build1, true );

        store.setBuildResult( buildId1, ContinuumProjectState.OK, result, null );

        txManager.commit();

        // --- -- -

        txManager.begin();

        store.removeProject( projectId1 );

        txManager.commit();

        // --- -- -

        txManager.begin();

        store.getBuild( buildId2 );

        store.getProject( projectId2 );

        txManager.commit();

        // --- -- -

        txManager.begin();

        try
        {
            store.getBuild( buildId1 );

            fail( "Expected exception" );
        }
        catch( ContinuumStoreException ex )
        {
            // expected
        }

        // --- -- -

        txManager.begin();

        try
        {
            store.getProject( projectId1 );

            fail( "Expected exception." );
        }
        catch( ContinuumStoreException ex )
        {
            // expected
        }

        // --- -- -

        txManager.begin();

        Iterator it = store.getAllProjects();

        assertTrue( it.hasNext() );

        it.next();

        assertFalse( it.hasNext() );

        txManager.commit();
    }

    public void testUpdateProjectWithValidParameters()
        throws Exception
    {
        String name1 = "Foo Project";

        String scmUrl1 = "scm:test:";

        String type = "maven2";

        String nagEmailAddress1 = "Nag Email Address 1";

        String version1 = "1.0";

        // --- -- -

        txManager.begin();

        String projectId = store.addProject( name1, scmUrl1, nagEmailAddress1, version1, type );

        txManager.commit();

        // --- -- -

        txManager.begin();

        ContinuumProject project = store.getProject( projectId );

        assertProject( projectId, name1, scmUrl1, nagEmailAddress1, type, project );

        String name2 = "Bar Project";

        String scmUrl2 = "Bar Project";

        String nagEmailAddress2 = "Nag Address 2";

        String version2 = "2.0";

        store.updateProject( projectId, name2, scmUrl2, nagEmailAddress2, version2 );

        project = store.getProject( projectId );

        assertProject( projectId, name2, scmUrl2, nagEmailAddress2, type, project );

        txManager.commit();
    }

    public void testUpdateProjectWithInvalidParameters()
        throws Exception
    {
        txManager.begin();

        String projectId = store.addProject( "Foo Project", "scm", "foo@bar", "1.0", "maven2" );

        txManager.commit();

        String name = "name";

        String scmUrl = "scmUrl";

        String nagEmailAddress = "nagEmailAddress";

        String version = "1.0";

        assertProjectUpdateFails( projectId, name, null, null, null );

        assertProjectUpdateFails( projectId, null, scmUrl, null, null );

        assertProjectUpdateFails( projectId, null, null, nagEmailAddress, null );

        assertProjectUpdateFails( projectId, null, null, null, version );
    }

    // ----------------------------------------------------------------------
    // Build tests
    // ----------------------------------------------------------------------

    public void testBuild()
        throws Exception
    {
        txManager.begin();

        String projectId = store.addProject( "Foo Project", "scm", "foo@bar", "1.0", "maven2" );

        String buildId = store.createBuild( projectId );

        txManager.commit();

        // --- -- -

        txManager.begin();

        ContinuumProject project = store.getProject( projectId );

        assertNotNull( project );

        assertEquals( ContinuumProjectState.BUILD_SIGNALED, project.getState() );

        ContinuumBuild build = store.getBuild( buildId );

        assertNotNull( build );

        assertEquals( ContinuumProjectState.BUILD_SIGNALED, build.getState() );

        Iterator it = store.getBuildsForProject( projectId, 0, 0 );

        List list = CollectionUtils.iteratorToList( it );

        assertEquals( 1, list.size() );

        build = (ContinuumBuild) list.get( 0 );

        assertBuildResult( projectId, buildId, ContinuumProjectState.BUILD_SIGNALED, build );

        txManager.commit();

        // --- -- -

        txManager.begin();

        store.setBuildResult( buildId, ContinuumProjectState.OK, null, null );

        txManager.commit();

        // --- -- -

        txManager.begin();

        build = store.getBuild( buildId );

        assertEquals( ContinuumProjectState.OK, build.getState() );

        txManager.commit();
    }

    public void testGetBuildsForProject()
    	throws Exception
    {
        txManager.begin();

        String projectId = createProject();

        List builds = new ArrayList();

        for ( int i = 0; i < 10; i++ )
        {
            builds.add( createBuild( projectId ) );
        }

        txManager.commit();

        txManager.begin();

        Iterator it = store.getBuildsForProject( projectId, 0, 0 );

        int i;

        for ( i = builds.size() - 1; it.hasNext(); i-- )
        {
            String expectedBuildId = (String) builds.get( i );

            ContinuumBuild actual = (ContinuumBuild) it.next();

            assertNotNull( actual );

            assertEquals( expectedBuildId, actual.getId() );
        }

        txManager.commit();
    }

    public void testGetLatestBuildForProject()
        throws Exception
    {
        // Add a project
        txManager.begin();

        String projectId = store.addProject( "Foo Project", "scm", "foo@bar", "1.0", "maven2" );

        txManager.commit();

        txManager.begin();

        assertNull( store.getLatestBuildForProject( projectId ) );

        txManager.commit();

        // make some builds
        txManager.begin();

        createBuild( projectId );

        createBuild( projectId );

        createBuild( projectId );

        createBuild( projectId );

        createBuild( projectId );

        String buildId6 = createBuild( projectId );

        txManager.commit();

        // check the latest build

        txManager.begin();

        ContinuumBuild build6 = store.getLatestBuildForProject( projectId );

        assertNotNull( build6 );

        assertEquals( buildId6, build6.getId() );

        txManager.commit();

        // Add some more builds

        txManager.begin();

        String buildId7 = createBuild( projectId );

        txManager.commit();

        // check the latest build

        txManager.begin();

        ContinuumBuild build7 = store.getLatestBuildForProject( projectId );

        assertEquals( buildId7, build7.getId() );

        txManager.commit();
    }

    // ----------------------------------------------------------------------
    // Assertions
    // ----------------------------------------------------------------------

    private void assertProject( String id, String name, String scmUrl, String nagEmailAddress, String type, ContinuumProject project )
    {
        assertNotNull( project );

        assertEquals( id, project.getId() );

        assertEquals( name, project.getName() );

        assertEquals( scmUrl, project.getScmUrl() );

        assertEquals( nagEmailAddress, project.getNagEmailAddress() );

        assertEquals( type, project.getType() );
    }

    private void assertProjectUpdateFails( String projectId, String name, String scmUrl, String nagEmailAddress, String version )
    {
        try
        {
            store.updateProject( projectId, name, scmUrl, nagEmailAddress, version );

            fail( "Expected exception" );
        }
        catch( ContinuumStoreException ex )
        {
            // expected
        }
    }

    private void assertBuildResult( String projectId, String buildId, ContinuumProjectState state, ContinuumBuild buildResult )
    {
        // Check the reloaded build result
        assertNotNull( buildResult );

        assertEquals( buildId, buildResult.getId() );

        assertEquals( projectId, buildResult.getProject().getId() );

        assertEquals( state, buildResult.getState() );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private int projectCount;

    private String createProject()
    	throws ContinuumStoreException
    {
        projectCount++;

        return store.addProject( "Project #" + projectCount, "scm:test:src/test/repository:simple-project", "foo@bar", "1.0", "maven2" );
    }

    private String createBuild( String projectId )
        throws ContinuumStoreException, InterruptedException
    {
        String buildId = store.createBuild( projectId );

        Thread.sleep( 10 );

        store.setBuildResult( buildId, ContinuumProjectState.OK, null, null );

        return buildId;
    }
}
