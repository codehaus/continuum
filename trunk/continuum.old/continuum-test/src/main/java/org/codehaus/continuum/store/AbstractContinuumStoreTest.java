package org.codehaus.continuum.store;

import java.util.Iterator;
import java.util.List;

import org.codehaus.continuum.TestUtils;
import org.codehaus.continuum.project.BuildResult;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ProjectDescriptor;
import org.codehaus.continuum.project.TestProjectDescriptor;
import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AbstractContinuumStoreTest.java,v 1.1 2004-07-19 16:54:47 trygvis Exp $
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

    public void setUp()
        throws Exception
    {
        super.setUp();

        store = (ContinuumStore) lookup( ContinuumStore.ROLE, getRoleHint() );
    }

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

        assertEquals( ContinuumProject.PROJECT_STATE_NEW, project.getState() );

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

    public void testBuildResult()
        throws Exception
    {
        beginTx();

        String projectId = store.addProject( "Foo Project", "scm", "maven2" );

        String buildId = store.createBuildResult( projectId );

        commitTx();

        beginTx();

        Iterator it = store.getBuildResultsForProject( projectId, 0, 0 );

        List list = TestUtils.iteratorToList( it );

        assertEquals( 1, list.size() );

        BuildResult buildResult = (BuildResult) list.get( 0 );

        assertBuildResult( projectId, buildId, BuildResult.BUILD_BUILDING, buildResult );

        commitTx();
    }

    private void assertBuildResult( String projectId, String buildId, int state, BuildResult buildResult )
    {
        // Check the reloaded build result
        assertNotNull( buildResult );

        assertEquals( buildId, buildResult.getBuildId() );

        assertEquals( projectId, buildResult.getProject().getId() );

        assertEquals( state, buildResult.getState() );
    }
}
