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

import org.apache.maven.Maven;
import org.codehaus.continuum.AbstractContinuumTest;
import org.codehaus.continuum.Continuum;
import org.codehaus.continuum.TestUtils;
import org.codehaus.continuum.buildqueue.BuildQueue;
import org.codehaus.continuum.maven.DefaultMavenTool;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumBuildResult;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ContinuumProjectState;
import org.codehaus.continuum.store.ContinuumStore;
import org.codehaus.continuum.store.tx.StoreTransactionManager;

import java.io.File;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: SimpleMaven2BuilderTest.java,v 1.9 2004-10-31 15:29:15 trygvis Exp $
 */
public class SimpleMaven2BuilderTest
    extends AbstractContinuumTest
{
    public void testContinuum()
        throws Exception
    {
        lookup( Maven.ROLE );

        Continuum continuum = getContinuum();

        BuildQueue queue = getBuildQueue();

        ContinuumStore store = getContinuumStore();

        StoreTransactionManager txManager = getStoreTransactionManager();

        String repo = "scm:local:src/test/repository:project1";

        txManager.begin();

        String projectId = continuum.addProjectFromScm( repo, "maven2" );

        ContinuumProject project = store.getProject( projectId );

        assertEquals( ContinuumProjectState.NEW, project.getState() );

        assertEquals( 0, queue.getLength() );

        txManager.commit();

        String buildId = continuum.buildProject( projectId );

        // NOTE: this test might fail if you have a slow system
        // because the builder thread might start before the return of the call.
        assertEquals( 1, queue.getLength() );

        ContinuumBuild result = TestUtils.waitForBuild( buildId );

        ContinuumBuildResult buildResult = result.getBuildResult();

        assertNotNull( buildResult );

        assertTrue( buildResult instanceof ExternalMaven2BuildResult );

        if ( !project.getState().equals( ContinuumProjectState.OK ) )
        {
            ExternalMaven2BuildResult m2Result = (ExternalMaven2BuildResult) buildResult;

            System.err.println( "************************" );

            System.err.println( m2Result.getStandardOutput() );

            System.err.println( "************************" );

            System.err.println( m2Result.getStandardError() );

            System.err.println( "************************" );
        }

        assertEquals( ContinuumProjectState.OK, project.getState() );

        assertEquals( 0, queue.getLength() );

//        String repository = ( (Maven2ContinuumBuilder) lookup( ContinuumBuilder.ROLE, "maven2" ) ).getMavenRepository();
        DefaultMavenTool mavenTool = (DefaultMavenTool) lookup( DefaultMavenTool.ROLE );

//        String repository = mavenTool.getMavenRepository();
//
//        File project1Jar = new File( repository, "plexus/jars/continuum-project1-1.0.jar" );
//
//        assertTrue( "Jar file doesn't exists: " + project1Jar.getAbsolutePath(), project1Jar.exists() );
//
//        File project1Pom = new File( repository, "plexus/poms/continuum-project1-1.0.pom" );
//
//        assertTrue( "Pom file doesn't exists: " + project1Pom.getAbsolutePath(), project1Pom.exists() );
    }
}
