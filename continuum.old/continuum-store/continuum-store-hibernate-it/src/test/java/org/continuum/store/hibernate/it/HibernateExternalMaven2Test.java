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
 * @version $Id: HibernateExternalMaven2Test.java,v 1.3 2004-10-24 20:39:09 trygvis Exp $
 */
public class HibernateExternalMaven2Test
    extends AbstractContinuumTest
{
    private String name = "Test Project";

    private String scmUrl = "scm:local:src/test/repository:maven2";

    private String nagEmailAddress = "given nag email address";

    private String version = "given version";

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

        String projectId = store.addProject( name, scmUrl, nagEmailAddress, version, builderType );

        txManager.commit();

        // ----------------------------------------------------------------------
        // Create a descriptor
        // ----------------------------------------------------------------------

        txManager.begin();

        ContinuumProject project = store.getProject( projectId );

        ProjectDescriptor desc = builder.createDescriptor( project );

        assertNotNull( desc );

        assertTrue( desc instanceof Maven2ProjectDescriptor );

        Maven2ProjectDescriptor descriptor = (Maven2ProjectDescriptor) desc;

        // Goals
        assertNotNull( descriptor.getGoals() );

        assertEquals( 2, descriptor.getGoals().size() );

        assertEquals( "clean:clean", descriptor.getGoals().get( 0 ) );

        assertEquals( "jar:install", descriptor.getGoals().get( 1 ) );

        // Name
        assertEquals( "IT Test Foo", descriptor.getName() );

        assertEquals( "IT Test Foo", project.getName() );

        // Nag email address
        assertEquals( "foo@bar", descriptor.getNagEmailAddress() );

        assertEquals( "foo@bar", project.getNagEmailAddress() );

        // Scm Url
        assertEquals( "scm:local:src/test/repository:maven2", descriptor.getScmUrl() );

        assertEquals( "scm:local:src/test/repository:maven2", project.getScmUrl() );

        // Version
        assertEquals( "1.0", descriptor.getVersion() );

        assertEquals( "1.0", project.getVersion() );

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
