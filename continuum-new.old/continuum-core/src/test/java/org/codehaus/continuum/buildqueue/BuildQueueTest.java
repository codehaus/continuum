package org.codehaus.continuum.buildqueue;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
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

import java.util.Properties;

import org.codehaus.continuum.store.ContinuumStore;
import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: BuildQueueTest.java,v 1.1 2005-02-22 10:12:19 trygvis Exp $
 */
public class BuildQueueTest
    extends PlexusTestCase
{
    private BuildQueue buildQueue;

    private ContinuumStore store;

    public void setUp()
        throws Exception
    {
        super.setUp();

        buildQueue = (BuildQueue) lookup( BuildQueue.ROLE );

        store = (ContinuumStore) lookup( ContinuumStore.ROLE );
    }

    public void testTestTheQueueWithASingleProject()
        throws Exception
    {
        String name = "Project 1";

        String project = createProject( name );

        String build = enqueue( project );

        assertNextBuildIs( build );

        assertNextBuildIsNull();

        String buildX = enqueue( project );

        enqueue( project );
        enqueue( project );
        enqueue( project );
        enqueue( project );

        assertNextBuildIs( buildX );

        assertNextBuildIsNull();
    }

    public void testTheQueueWithMultipleProjects()
        throws Exception
    {
        String name1 = "Project 1";

        String name2 = "Project 2";

        String project1 = createProject( name1 );

        String project2 = createProject( name2 );

        String build1 = enqueue( project1 );

        String build2 = enqueue( project2 );

        assertNextBuildIs( build1 );

        assertNextBuildIs( build2 );

        assertNextBuildIsNull();

        String buildX1 = enqueue( project1 );

        String buildX2 = enqueue( project2 );

        enqueue( project1 );
        enqueue( project2 );
        enqueue( project1 );
        enqueue( project2 );
        enqueue( project1 );
        enqueue( project2 );
        enqueue( project1 );
        enqueue( project2 );

        assertNextBuildIs( buildX1 );

        assertNextBuildIs( buildX2 );

        assertNextBuildIsNull();
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private String createProject( String name )
        throws Exception
    {
        String scmUrl = "scm:local:src/test/projects/project-1";
        String nagEmailAddress = "foo@bar";
        String version = "1.0";
        String builderId = "test";
        String workingDirectory = getTestPath( "target/checkouts" );
        Properties properties = new Properties();

        return store.addProject( name, scmUrl, nagEmailAddress, version, builderId, workingDirectory, properties );
    }

    private String enqueue( String projectId )
        throws Exception
    {
        String buildId = store.createBuild( projectId );

        buildQueue.enqueue( projectId, buildId );

        return buildId;
    }

    private void assertNextBuildIs( String expectedBuildId )
        throws Exception
    {
        String actualBuildId = buildQueue.dequeue();

        assertNotNull( "Got a null build id returned.", actualBuildId );

        assertEquals( "Didn't get the expected build id.", expectedBuildId, actualBuildId );
    }

    private void assertNextBuildIsNull()
        throws Exception
    {
        String actualBuildId = buildQueue.dequeue();

        assertNull( "Got a non-null build id returned: " + actualBuildId, actualBuildId );
    }
}
