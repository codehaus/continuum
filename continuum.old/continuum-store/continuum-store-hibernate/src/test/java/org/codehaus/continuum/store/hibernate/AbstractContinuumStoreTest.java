package org.codehaus.continuum.store.hibernate;

/*
 * LICENSE
 */

import org.codehaus.continuum.project.BuildResult;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.MockProjectDescriptor;
import org.codehaus.continuum.project.ProjectDescriptor;
import org.codehaus.continuum.store.ContinuumStore;
import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AbstractContinuumStoreTest.java,v 1.1 2004-07-07 02:34:38 trygvis Exp $
 */
public abstract class AbstractContinuumStoreTest
    extends PlexusTestCase
{
    private ContinuumStore store;

    protected abstract String getRoleHint();

    protected abstract void connect()
        throws Exception;

    protected abstract void disconnect()
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

        connect();

        String id = store.addProject( projectName, scmConnection, "maven2" );

        disconnect();

        connect();

        ContinuumProject project = store.getProject( id );

        // Check a 'blank' project

        assertNotNull( project );

        assertEquals( id, project.getId() );

        assertEquals( projectName, project.getName() );

        assertEquals( scmConnection, project.getScmConnection() );

        assertEquals( ContinuumProject.PROJECT_STATE_NEW, project.getState() );

        assertNull( project.getDescriptor() );

        disconnect();

        // Check the descriptor

        connect();

        project = store.getProject( id );

        MockProjectDescriptor descriptor = new MockProjectDescriptor();

        descriptor.setProjectId( id );

        descriptor.setProject( project );

        descriptor.setAttribute( 666 );

        store.setProjectDescriptor( id, descriptor );

        disconnect();

        connect();

        project = store.getProject( id );

        assertNotNull( project );

        ProjectDescriptor desc = project.getDescriptor();

        assertNotNull( desc );

        assertEquals( MockProjectDescriptor.class.getName(), desc.getClass().getName() );

        descriptor = (MockProjectDescriptor)desc;

        assertEquals( 666, descriptor.getAttribute() );

        disconnect();
    }

    public void testBuildResult()
        throws Exception
    {
        connect();

        String projectId = store.addProject( "Foo Project", "scm", "maven2" );

        String buildId = store.createBuildResult( projectId );

        assertNotNull( buildId );

        disconnect();

        connect();

        BuildResult buildResult = store.getBuildResult( buildId );

        // Check the reloaded build result
        assertNotNull( buildResult );

        assertEquals( buildId, buildResult.getBuildId() );

        assertEquals( projectId, buildResult.getProject().getId() );

        assertEquals( BuildResult.BUILD_BUILDING, buildResult.getState() );

        disconnect();
    }
}
