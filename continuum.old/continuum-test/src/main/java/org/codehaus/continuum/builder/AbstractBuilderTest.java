package org.codehaus.continuum.builder;

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

import org.codehaus.continuum.AbstractContinuumTest;
import org.codehaus.continuum.Continuum;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumProjectState;
import org.codehaus.continuum.store.ContinuumStore;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AbstractBuilderTest.java,v 1.2 2004-07-29 04:27:41 trygvis Exp $
 */
public abstract class AbstractBuilderTest
    extends AbstractContinuumTest
{
    protected abstract String getProjectType();

    public void testNormalTestCase()
        throws Exception
    {
        Continuum continuum = getContinuum();

        ContinuumStore store = getContinuumStore();

        String scm = "scm:test:src/test/projects/" + getProjectType() + ":normal";

        String projectId = continuum.addProject( "Normal project", scm, getProjectType() );

        String buildId = continuum.buildProject( projectId );

        ContinuumBuild buildResult = store.getBuild( buildId );

        assertEquals( ContinuumProjectState.OK, buildResult.getState() );
    }

    private void waitForBuild()
        throws Exception
    {
        Continuum continuum = getContinuum();

        int count = 10;

        while ( continuum.getBuildQueueLength() > 0 )
        {
            Thread.sleep( 100 );

            if ( count-- == 0 )
            {
                fail( "Timeout while waiting for build." );
            }
        }
    }
}
