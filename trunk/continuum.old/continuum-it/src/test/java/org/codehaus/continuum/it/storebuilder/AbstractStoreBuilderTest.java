package org.codehaus.continuum.it.storebuilder;

/*
 * LICENSE
 */

import java.io.File;

import org.codehaus.continuum.AbstractContinuumTest;
import org.codehaus.continuum.Continuum;
import org.codehaus.continuum.TestUtils;
import org.codehaus.continuum.builder.ContinuumBuilder;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.store.ContinuumStore;
import org.codehaus.continuum.store.tx.StoreTransactionManager;
import org.codehaus.plexus.util.FileUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AbstractStoreBuilderTest.java,v 1.1 2004-10-29 15:31:27 trygvis Exp $
 */
public abstract class AbstractStoreBuilderTest
	extends AbstractContinuumTest
{
    private Class builderClass;
    private Class storeClass;

    private ContinuumBuilder builder;
    private String builderHint;
    private ContinuumStore store;

    private String scmUrl;

    public AbstractStoreBuilderTest( Class builderClass, String builderHint, Class storeClass, String storeHint )
    {
        this.builderClass = builderClass;
        this.builderHint = builderHint;
        this.storeClass = storeClass;

        scmUrl = "scm:local:target/repositories/store-builder/:" + builderHint + "-" + storeHint;
    }

    public void setUp() throws Exception
    {
        super.setUp();

        builder = getContinuumBuilder( builderHint );
        store = getContinuumStore();

        assertEquals( builderClass, builder.getClass() );
        assertEquals( storeClass, store.getClass() );
    }

    // ----------------------------------------------------------------------
    // 
    // ----------------------------------------------------------------------

    protected abstract void assertProject( ContinuumProject project )
    	throws Exception;

    protected abstract void assertBuild( ContinuumBuild build )
    	throws Exception;

    // ----------------------------------------------------------------------
    // 
    // ----------------------------------------------------------------------

    public void testStoreBuilder()
    	throws Exception
    {
        // ----------------------------------------------------------------------
        // Copy the repository
        // ----------------------------------------------------------------------

        File repositories = getTestFile( "src/test/repositories" );

        FileUtils.copyDirectoryStructure( repositories, getTestFile( "target/repositories" ) );

        // ----------------------------------------------------------------------
        // Add the project
        // ----------------------------------------------------------------------

        Continuum continuum = getContinuum();

        StoreTransactionManager txManager = getStoreTransactionManager();

        txManager.begin();

        String projectId = continuum.addProjectFromScm( scmUrl, builderHint );

        txManager.commit();

        // ----------------------------------------------------------------------
        // Assert the descriptor
        // ----------------------------------------------------------------------

        txManager.begin();

        ContinuumProject project = continuum.getProject( projectId );

        assertNotNull( project );

        assertNotNull( project.getDescriptor() );

        assertProject( project );

        txManager.commit();

        String buildId = continuum.buildProject( projectId );

        ContinuumBuild build = TestUtils.waitForBuild( buildId );

        // ----------------------------------------------------------------------
        // Assert the build
        // ----------------------------------------------------------------------

        assertBuild( build );
    }
}
