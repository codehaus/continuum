package org.continuum.store.hibernate.it;

/*
 * LICENSE
 */

import java.net.URL;

import org.codehaus.continuum.AbstractContinuumTest;
import org.codehaus.continuum.Continuum;
import org.codehaus.continuum.TestUtils;
import org.codehaus.continuum.builder.ContinuumBuilder;
import org.codehaus.continuum.builder.maven2.ExternalMaven2BuildResult;
import org.codehaus.continuum.builder.maven2.ExternalMaven2ContinuumBuilder;
import org.codehaus.continuum.builder.maven2.Maven2ProjectDescriptor;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ContinuumProjectState;
import org.codehaus.continuum.project.ProjectDescriptor;
import org.codehaus.continuum.store.ContinuumStore;
import org.codehaus.continuum.store.hibernate.HibernateContinuumStore;
import org.codehaus.continuum.store.hibernate.HibernateUtils;
import org.codehaus.continuum.store.hibernate.tx.HibernateStoreTransactionManager;
import org.codehaus.continuum.store.tx.StoreTransactionManager;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: HibernateExternalMaven2Test.java,v 1.1 2004-10-06 13:52:41 trygvis Exp $
 */
public class HibernateExternalMaven2Test
    extends AbstractContinuumTest
{
    private String name = "Test Project";

    private String scmUrl = "scm:test:src/test/repository:maven2";

    private String builderType;

    public void setUp()
        throws Exception
    {
        super.setUp();

        builderType = "maven2";

        setUpHibernate();
    }

    protected void setUpHibernate()
        throws Exception
    {
        URL hibernateCfg = Thread.currentThread().getContextClassLoader().getResource( "/hibernate.cfg.xml" );

        assertNotNull( "Could not find the hibernate configuration.", hibernateCfg );

        HibernateUtils.createDatabase( hibernateCfg );
    }

    public void testFoo()
        throws Exception
    {
        Continuum continuum = getContinuum();

        ContinuumStore store = getContinuumStore();

        StoreTransactionManager txManager = getStoreTransactionManager();

        assertEquals( HibernateContinuumStore.class, store.getClass() );

        assertEquals( HibernateStoreTransactionManager.class, txManager.getClass() );

        ContinuumBuilder builder = getContinuumBuilder( builderType );

        assertEquals( ExternalMaven2ContinuumBuilder.class, builder.getClass() );

        // ----------------------------------------------------------------------
        // Add the project
        // ----------------------------------------------------------------------

        txManager.begin();

        String projectId = store.addProject( name, scmUrl, builderType );

        txManager.commit();

        // ----------------------------------------------------------------------
        // Create a descriptor
        // ----------------------------------------------------------------------

        txManager.begin();

        ContinuumProject project = store.getProject( projectId );

        ProjectDescriptor descriptor = builder.createDescriptor( project );

        assertNotNull( descriptor );

        assertTrue( descriptor instanceof Maven2ProjectDescriptor );

        Maven2ProjectDescriptor maven2ProjectDescriptor = (Maven2ProjectDescriptor) descriptor;

        // Goals
        assertNotNull( maven2ProjectDescriptor.getGoals() );

        assertEquals( 2, maven2ProjectDescriptor.getGoals().size() );

        assertEquals( "clean:clean", maven2ProjectDescriptor.getGoals().get( 0 ) );

        assertEquals( "jar:install", maven2ProjectDescriptor.getGoals().get( 1 ) );

        // MavenProject
//        assertNotNull( maven2ProjectDescriptor.getMavenProject() );

        store.setProjectDescriptor( projectId, descriptor );

        txManager.commit();

        // ----------------------------------------------------------------------
        // Build it
        // ----------------------------------------------------------------------

        String buildId = continuum.buildProject( projectId );

        ContinuumBuild build = TestUtils.waitForBuild( buildId );

        ExternalMaven2BuildResult result = (ExternalMaven2BuildResult) build.getBuildResult();

        if ( build.getState() != ContinuumProjectState.OK )
        {
            System.err.println( "************************" );
            System.err.println( result.getStandardOutput() );
            System.err.println( "************************" );
            System.err.println( result.getStandardError() );
            System.err.println( "************************" );
        }

        assertEquals( ContinuumProjectState.OK, build.getState() );

        assertEquals( result.getExitCode(), 0 );

        assertNotNull( result.getStandardOutput() );

        assertTrue( result.getStandardOutput().length() > 0 );

        assertNotNull( result.getStandardError() );

        assertTrue( result.getStandardError().length() == 0 );
    }
}
