package org.codehaus.continuum;

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

import org.codehaus.continuum.buildqueue.BuildQueue;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ContinuumProjectState;
import org.codehaus.continuum.store.ContinuumStore;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultContinuumTest.java,v 1.22 2004-07-27 05:42:14 trygvis Exp $
 */
public class DefaultContinuumTest
    extends AbstractContinuumTest
{
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
    }
}
