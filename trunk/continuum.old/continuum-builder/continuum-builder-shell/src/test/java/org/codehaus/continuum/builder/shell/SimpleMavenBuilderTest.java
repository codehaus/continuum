package org.codehaus.continuum.builder.shell;

/*
 * LICENSE
 */

import java.io.File;
import java.util.List;

import org.codehaus.continuum.AbstractContinuumTest;
import org.codehaus.continuum.Continuum;
import org.codehaus.continuum.TestUtils;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.store.ContinuumStore;
import org.codehaus.continuum.store.tx.StoreTransactionManager;
import org.codehaus.plexus.util.CollectionUtils;
import org.codehaus.plexus.util.FileUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: SimpleMavenBuilderTest.java,v 1.2 2004-10-30 14:45:24 trygvis Exp $
 */
public class SimpleMavenBuilderTest
	extends AbstractContinuumTest
{
    private Continuum continuum;

    private ContinuumStore store;

    private StoreTransactionManager txManager;

    public void setUp()
    	throws Exception
    {
        super.setUp();

        continuum = getContinuum();

        store = getContinuumStore();

        txManager = getStoreTransactionManager();
    }

    public void testSimpleTestOfTheMavenBuilder()
    	throws Exception
    {
        // ----------------------------------------------------------------------
        // Copy the repository
        // ----------------------------------------------------------------------

        File repositories = getTestFile( "src/test/repository" );

        FileUtils.copyDirectoryStructure( repositories, getTestFile( "target/repository" ) );

        // ----------------------------------------------------------------------
        // 
        // ----------------------------------------------------------------------

        txManager.begin();

        String projectId = continuum.addProjectFromScm( "scm:local:target/repository:simple", "maven" );

        txManager.commit();

        String buildId = continuum.buildProject( projectId );

        TestUtils.setBuildTimeout( 120 * 1000 ); // two minutes

        TestUtils.waitForSuccessfulBuild( buildId );

        txManager.begin();

        ContinuumProject project = store.getProject( projectId );

        List list = CollectionUtils.iteratorToList( store.getBuildsForProject( projectId, 0, 0 ) );

        assertTrue( list.size() == 1 );

        ContinuumBuild build = (ContinuumBuild) list.get( 0 );

        assertNotNull( build.getBuildResult() );

        assertTrue( build.getBuildResult() instanceof ShellBuildResult );

        ShellBuildResult result = (ShellBuildResult) build.getBuildResult();

        assertEquals( 0, result.getExitCode() );

        if ( false )
        {
            System.err.println( result.getStandardOutput() );
        }

        assertEquals( "", result.getStandardError() );

        File workingDirectory = new File( project.getWorkingDirectory() );

        txManager.commit();

        System.err.println( workingDirectory.getAbsolutePath() );
    }
}
