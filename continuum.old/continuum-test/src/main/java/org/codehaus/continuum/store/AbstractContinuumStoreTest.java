package org.codehaus.continuum.store;

import java.util.Iterator;
import java.util.List;

import org.codehaus.continuum.TestUtils;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ContinuumProjectState;
import org.codehaus.continuum.project.ProjectDescriptor;
import org.codehaus.continuum.project.TestProjectDescriptor;
import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AbstractContinuumStoreTest.java,v 1.2 2004-07-27 00:06:11 trygvis Exp $
 */
public abstract class AbstractContinuumStoreTest
    extends PlexusTestCase
{
    private ContinuumStore store;

    protected abstract String getRoleHint();

    protected abstract void beginTx()
        throws Exception;

    protected abstract void commitTx()
        throws Exception;

    protected abstract void rollbackTx()
        throws Exception;

    public void setUp()
        throws Exception
    {
        super.setUp();

        store = (ContinuumStore) lookup( ContinuumStore.ROLE, getRoleHint() );
    }

    // ----------------------------------------------------------------------
    // Project tests
    // ----------------------------------------------------------------------

    public void testContinuumProject()
        throws Exception
    {
        String projectName = "Foo Project";

        String scmConnection = "scm:cvs:local:ignored:/cvsroot:module";

        String type = "mock";

        beginTx();

        String id = store.addProject( projectName, scmConnection, type );

        commitTx();

        beginTx();

        ContinuumProject project = store.getProject( id );

        // Check a 'blank' project

        assertNotNull( project );

        assertEquals( id, project.getId() );

        assertEquals( projectName, project.getName() );

        assertEquals( scmConnection, project.getScmConnection() );

        assertEquals( type, project.getType() );

        assertEquals( ContinuumProjectState.NEW, project.getState() );

        assertNull( project.getDescriptor() );

        commitTx();

        // Check the descriptor

        beginTx();

        project = store.getProject( id );

        TestProjectDescriptor descriptor = new TestProjectDescriptor();

        descriptor.setProjectId( id );

        descriptor.setProject( project );

        descriptor.setAttribute( 666 );

        store.setProjectDescriptor( id, descriptor );

        commitTx();

        beginTx();

        project = store.getProject( id );

        assertNotNull( project );

//        assertEquals( type, project.getType() );

        ProjectDescriptor desc = project.getDescriptor();

        assertNotNull( desc );

        assertEquals( TestProjectDescriptor.class.getName(), desc.getClass().getName() );

        descriptor = (TestProjectDescriptor)desc;

        assertEquals( 666, descriptor.getAttribute() );

        commitTx();
    }

    public void testUpdateProjectWithValidParameters()
        throws Exception
    {
        String name1 = "Foo Project";

        String scmConnection1 = "scm:test:";

        String type = "maven2";

        beginTx();

        String projectId = store.addProject( name1, scmConnection1, type );

        commitTx();

        beginTx();

        ContinuumProject project = store.getProject( projectId );

        assertProject( projectId, name1, scmConnection1, type, project );

        String name2 = "Bar Project";

        String scmConnection2 = "Bar Project";

        store.updateProject( projectId, name2, scmConnection2 );

        project = store.getProject( projectId );

        assertProject( projectId, name2, scmConnection2, type, project );

        commitTx();
    }

    public void testUpdateProjectWithInvalidParameters()
        throws Exception
    {
        beginTx();

        String projectId = store.addProject( "Foo Project", "scm", "maven2" );

        commitTx();

        assertProjectUpdateFails( projectId, null, null );

        assertProjectUpdateFails( projectId, "foo", null );

        assertProjectUpdateFails( projectId, null, null );
    }

    // ----------------------------------------------------------------------
    // Build tests
    // ----------------------------------------------------------------------

    public void testBuild()
        throws Exception
    {
        beginTx();

        String projectId = store.addProject( "Foo Project", "scm", "maven2" );

        String buildId = store.createBuild( projectId );

        commitTx();

        beginTx();

        ContinuumProject project = store.getProject( projectId );

        ContinuumBuild build = store.getBuild( buildId );

        assertEquals( ContinuumProjectState.BUILD_SIGNALED, project.getState() );

        assertEquals( ContinuumProjectState.BUILD_SIGNALED, build.getState() );

        Iterator it = store.getBuildsForProject( projectId, 0, 0 );

        List list = TestUtils.iteratorToList( it );

        assertEquals( 1, list.size() );

        build = (ContinuumBuild) list.get( 0 );

        assertBuildResult( projectId, buildId, ContinuumProjectState.BUILD_SIGNALED, build );

        commitTx();

        beginTx();

        store.setBuildResult( buildId, ContinuumProjectState.OK, null, null );

        commitTx();

        beginTx();

        build = store.getBuild( buildId );

        assertEquals( ContinuumProjectState.OK, build.getState() );

        commitTx();
    }

    // ----------------------------------------------------------------------
    // Assertions
    // ----------------------------------------------------------------------

    private void assertProject( String id, String name, String scmConnection, String type, ContinuumProject project )
    {
        assertNotNull( project );

        assertEquals( id, project.getId() );

        assertEquals( name, project.getName() );

        assertEquals( scmConnection, project.getScmConnection() );

        assertEquals( type, project.getType() );
    }

    private void assertProjectUpdateFails( String projectId, String name, String scmUrl )
    {
        try
        {
            store.updateProject( projectId, name, scmUrl );

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
}
