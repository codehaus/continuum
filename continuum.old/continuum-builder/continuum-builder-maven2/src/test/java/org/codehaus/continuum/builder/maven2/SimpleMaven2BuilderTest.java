package org.codehaus.continuum.builder.maven2;

/*
 * Copyright (c) 2004, Jason van Zyl and Trygve Laugstøl
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

import java.io.File;

import org.apache.maven.MavenTestUtils;

import org.codehaus.continuum.AbstractContinuumTest;
import org.codehaus.continuum.Continuum;
import org.codehaus.continuum.buildqueue.BuildQueue;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ContinuumProjectState;
import org.codehaus.continuum.store.ContinuumStore;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.util.FileUtils;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: SimpleMaven2BuilderTest.java,v 1.1 2004-08-29 18:45:16 trygvis Exp $
 */
public class SimpleMaven2BuilderTest
    extends AbstractContinuumTest
{
    private File repository;

    private File mavenRepository;

    public void setUp()
        throws Exception
    {
        super.setUp();

        repository = getTestFile( "target/repository" );

        FileUtils.deleteDirectory( repository );

        File userHome = new File( System.getProperty( "user.home" ) );

        mavenRepository = new File( userHome, ".maven/repository" );

        if ( !mavenRepository.exists() )
        {
            mavenRepository = new File( userHome, ".m2/repository" );

            if ( !mavenRepository.exists() )
            {
                fail( "Can't find a maven repository." );
            }
        }

        copyArtifact( "maven/plugins/maven-clean-plugin-1.0-SNAPSHOT.jar" );

        copyArtifact( "maven/plugins/maven-jar-plugin-1.0-SNAPSHOT.jar" );

        copyArtifact( "maven/plugins/maven-pom-plugin-1.0-SNAPSHOT.jar" );

        copyArtifact( "maven/poms/maven-model-2.0-SNAPSHOT.pom" );

        copyArtifact( "maven/poms/maven-core-2.0-SNAPSHOT.pom" );

        copyArtifact( "maven/poms/maven-plugin-2.0-SNAPSHOT.pom" );
    }

    private void copyArtifact( String artifact )
        throws Exception
    {
        assertTrue( mavenRepository.exists() );

        File file = new File( mavenRepository, artifact );

        assertTrue( "Can't find artifact: " + file.getAbsolutePath(), file.exists() );

        File directory = new File( repository, new File( artifact ).getParentFile().getPath() );

        FileUtils.copyFileToDirectory( file, directory );
    }

    protected PlexusContainer getContainerInstance()
    {
        return MavenTestUtils.getContainerInstance();
    }

    protected void customizeContext()
        throws Exception
    {
        MavenTestUtils.customizeContext( getContainer(), getTestFile( "" ) );
    }

    public void testContinuum()
        throws Exception
    {
        Continuum continuum = getContinuum();

        BuildQueue queue = getBuildQueue();

        ContinuumStore store = getContinuumStore();

        String repo = "scm:cvs:local:ignored:" + getTestFile( "src/test/repository/" ) + ":project1";

        String projectId = continuum.addProject( "Continuum Test Project 1", repo, "maven2" );

        ContinuumProject project = store.getProject( projectId );

        assertEquals( ContinuumProjectState.NEW, project.getState() );

        assertEquals( 0, queue.getLength() );

        String buildId = continuum.buildProject( projectId );

        // NOTE: this test might fail if you have a slow system
        // because the builder thread might start before the return of the call.
        assertEquals( 1, queue.getLength() );

        int time = 30 * 1000;

        int interval = 100;

        ContinuumBuild result = null;

        while( time > 0 )
        {
            Thread.sleep( interval );

            time -= interval;

            result = store.getBuild( buildId );

            if ( result.getState() != ContinuumProjectState.BUILDING )
            {
                break;
            }
        }

        if ( time <= 0 )
        {
            fail( "Timeout while waiting for the build to finnish." );
        }

        assertEquals( ContinuumProjectState.OK, project.getState() );

        assertEquals( 0, queue.getLength() );

        File project1Jar = new File( repository, "plexus/jars/continuum-project1-1.0.jar" );

        assertTrue( project1Jar.exists() );

        File project1Pom = new File( repository, "plexus/poms/continuum-project1-1.0.pom" );

        assertTrue( project1Pom.exists() );
    }
}
