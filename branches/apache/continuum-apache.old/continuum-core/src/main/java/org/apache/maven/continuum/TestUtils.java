package org.apache.maven.continuum;

/*
 * Copyright 2004-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import org.apache.maven.continuum.project.ContinuumBuild;
import org.apache.maven.continuum.project.ContinuumProjectState;
import org.apache.maven.continuum.store.ContinuumStore;

import org.codehaus.plexus.PlexusTestCase;

import junit.framework.Assert;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: TestUtils.java,v 1.1.1.1 2005-03-29 20:41:59 trygvis Exp $
 */
public class TestUtils
{
    private static int buildTimeout = 30 * 1000;

    // ----------------------------------------------------------------------
    // Wait for build
    // ----------------------------------------------------------------------

    public final static ContinuumBuild waitForSuccessfulBuild( ContinuumStore continuumStore, String buildId )
        throws Exception
    {
        ContinuumBuild build = waitForBuild( continuumStore, buildId );

        Assert.assertEquals( ContinuumProjectState.OK, build.getState() );

        return build;
    }

    public final static ContinuumBuild waitForFailedBuild( ContinuumStore continuumStore, String buildId )
        throws Exception
    {
        ContinuumBuild build = waitForBuild( continuumStore, buildId );

        Assert.assertEquals( ContinuumProjectState.FAILED, build.getState() );

        return build;
    }

    public final static ContinuumBuild waitForBuild( ContinuumStore continuumStore, String buildId )
        throws Exception
    {
        int time = buildTimeout;

        int interval = 100;

        ContinuumBuild result;

        while ( time > 0 )
        {
            Thread.sleep( interval );

            time -= interval;

            result = continuumStore.getBuild( buildId );

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
    //
    // ----------------------------------------------------------------------
/*
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
*/
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
