package org.codehaus.continuum;

/*
 * The MIT License
 *
 * Copyright (c) 2004, Jason van Zyl and Trygve Laugst�l
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

import junit.framework.Assert;

import org.codehaus.continuum.project.ContinuumProjectState;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.store.ContinuumStore;
import org.codehaus.plexus.DefaultArtifactEnabledContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.logging.AbstractLogger;
import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Locale;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: TestUtils.java,v 1.2 2005-02-21 14:58:09 trygvis Exp $
 */
public class TestUtils
{
    private static PlexusContainer container;

    private static int buildTimeout = 30 * 1000;

    public static void setContainer( PlexusContainer container )
    {
        TestUtils.container = container;
    }

    protected final static PlexusContainer getContainer()
    {
        if ( TestUtils.container != null )
        {
            return TestUtils.container;
        }

        PlexusContainer container = AbstractContinuumTest.getPlexusContainer();

        Assert.assertNotNull( "This method can only be used when the test case is a subclass of AbstractContinuumTest or a container has been set explicitly.", container );

        return container;
    }

    // ----------------------------------------------------------------------
    // Wait for build
    // ----------------------------------------------------------------------

    public final static ContinuumBuild waitForSuccessfulBuild( String buildId )
        throws Exception
    {
        ContinuumBuild build = waitForBuild( buildId );

        Assert.assertEquals( ContinuumProjectState.OK, build.getState() );

        return build;
    }

    public final static ContinuumBuild waitForFailedBuild( String buildId )
        throws Exception
    {
        ContinuumBuild build = waitForBuild( buildId );

        Assert.assertEquals( ContinuumProjectState.FAILED, build.getState() );

        return build;
    }

    public final static ContinuumBuild waitForBuild( String buildId )
        throws Exception
    {
        int time = buildTimeout;

        int interval = 100;

        ContinuumBuild result;

        while ( time > 0 )
        {
            Thread.sleep( interval );

            time -= interval;

            result = getContinuumStore().getBuild( buildId );

            Assert.assertNotNull( result );

            if ( result.getState() == ContinuumProjectState.BUILD_SIGNALED )
            {
                continue;
            }

            if ( result.getState() != ContinuumProjectState.BUILDING )
            {
                return result;
            }
        }

        Assert.assertTrue( "Timeout while waiting for build. Build id: " + buildId, time > 0 );

        return null; // will never happen
    }

    public static void setBuildTimeout( int buildTimeout )
    {
        TestUtils.buildTimeout = buildTimeout;
    }

    // ----------------------------------------------------------------------
    // Lookups
    // ----------------------------------------------------------------------

    public static ContinuumStore getContinuumStore()
        throws Exception
    {
        return (ContinuumStore) getContainer().lookup( ContinuumStore.ROLE );
    }

    public static ContinuumStore getContinuumStore( String role )
        throws Exception
    {
        return (ContinuumStore) getContainer().lookup( ContinuumStore.ROLE, role );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public static PlexusContainer setUpPlexus()
        throws Exception
    {
        PlexusContainer container = new DefaultArtifactEnabledContainer();

        File basedir = new File( System.getProperty( "basedir", new File( "" ).getAbsolutePath() ) );

        File mavenHome = new File( System.getProperty( "user.home" ), "m2" );

        File mavenHomeLocal = new File( System.getProperty( "user.home" ), ".m2" );

        File repository = new File( System.getProperty( "user.home" ), ".m2/repository" );

        Assert.assertTrue( "Cannot find maven.home: " + mavenHome, mavenHome.exists() );

        Assert.assertTrue( "Cannot find maven.home.local: " + mavenHomeLocal, mavenHomeLocal.exists() );

        Assert.assertTrue( "Cannot find maven.repo.local: " + repository, repository.exists() );

        System.setProperty( "maven.home", mavenHome.getAbsolutePath() );

        System.setProperty( "maven.home.local", mavenHomeLocal.getAbsolutePath() );

        System.setProperty( "maven.repo.local", repository.getAbsolutePath() );

        // ----------------------------------------------------------------------
        // Set up the container
        // ----------------------------------------------------------------------

        container.getContext().put( "basedir", basedir.getAbsolutePath() );

        deleteFile( "continuumdb.log" );

        deleteFile( "continuumdb.properties" );

        deleteFile( "continuumdb.sql" );

        File plexusHome = PlexusTestCase.getTestFile( "target/plexus-home" );

        if ( plexusHome.exists() )
        {
            FileUtils.deleteDirectory( plexusHome );
        }

        container.getContext().put( "plexus.home", plexusHome.getAbsolutePath() );

        FileUtils.mkdir( new File( plexusHome, "logs" ).getAbsolutePath() );

        // ----------------------------------------------------------------------
        // Set up the maven 2 application
        // ----------------------------------------------------------------------

        File maven2 = new File( plexusHome, "apps/maven2" );

        Assert.assertTrue( "Coult not make directory " + maven2.getAbsolutePath() + "/core.", new File( maven2, "core" ).mkdirs() );

        Assert.assertTrue( "Coult not make directory " + maven2.getAbsolutePath() + "/bin.", new File( maven2, "bin" ).mkdirs() );

        FileUtils.copyFileToDirectory( new File( mavenHome, "core/classworlds-1.1-SNAPSHOT.jar" ), new File( maven2, "core" ) );

        FileUtils.copyFileToDirectory( new File( mavenHome, "bin/classworlds.conf" ), new File( maven2, "bin" ) );

        FileUtils.copyDirectoryStructure( new File( repository, "surefire" ), new File( plexusHome, "apps/maven2/repository/surefire" ) );

        // ----------------------------------------------------------------------
        // Set up the continuum application
        // ----------------------------------------------------------------------

        FileUtils.mkdir( new File( plexusHome, "apps/continuumweb/lib" ).getAbsolutePath() );

        FileUtils.mkdir( new File( plexusHome, "apps/continuumweb/web" ).getAbsolutePath() );

        File webDir = PlexusTestCase.getTestFile( "src/main/resources/web/continuumweb" );

        Assert.assertTrue( "Could not find " + webDir.getAbsolutePath(), webDir.isDirectory() );

        FileUtils.copyDirectoryStructure( webDir, new File( plexusHome, "apps/continuumweb/web/continuumweb" ) );

        File formsDir = PlexusTestCase.getTestFile( "src/main/resources/forms" );

        Assert.assertTrue( "Could not find " + formsDir.getAbsolutePath(), formsDir.isDirectory() );

        FileUtils.copyDirectoryStructure( formsDir, new File( plexusHome, "apps/continuumweb/forms" ) );

        File localizationDir = PlexusTestCase.getTestFile( "src/main/resources/localization" );

        Assert.assertTrue( "Could not find " + localizationDir.getAbsolutePath(), localizationDir.isDirectory() );

        FileUtils.copyDirectoryStructure( localizationDir, new File( plexusHome, "apps/continuumweb/localization" ) );

        // ----------------------------------------------------------------------
        // Set up the container
        // ----------------------------------------------------------------------

        Locale.setDefault( Locale.ENGLISH );

        container.setConfigurationResource( getCustomConfiguration() );

        container.initialize();

        container.start();

        LoggerManager loggerManager = (LoggerManager) container.lookup( LoggerManager.ROLE );

        loggerManager.setThreshold( AbstractLogger.LEVEL_DEBUG );

        return container;
    }

    private static void deleteFile( String fileName )
        throws Exception
    {
        File file = PlexusTestCase.getTestFile( fileName );

        if ( file.exists() )
        {
            Assert.assertTrue( "Error while deleting " + file.getAbsolutePath(), file.delete() );
        }
    }

    private static Reader getCustomConfiguration()
        throws Exception
    {
        URL config = Thread.currentThread().getContextClassLoader().getResource( "conf/plexus.conf" );

        Assert.assertNotNull( "Missing configuration.", config );

        return new InputStreamReader( config.openStream() );
    }
}
