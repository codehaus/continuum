package org.codehaus.continuum.cli;

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

import java.util.Iterator;

import junit.framework.TestCase;

import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.store.ContinuumStore;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AddProjectCliCommandTest.java,v 1.1 2004-10-09 11:21:27 trygvis Exp $
 */
public class AddProjectCliCommandTest
    extends TestCase
{
    public void testExecution()
        throws Exception
    {
        String[] args = new String[]{
            "addProject",
            "--project-name=Foo Project",
            "--scm-connection=scm:cvs:local:ignored:/cvsroot/:cvs-module",
            "--project-type=maven2"
        };

        ContinuumCC cli = new ContinuumCC();

        cli.start();

        ContinuumStore store = (ContinuumStore) cli.getPlexus().lookup( ContinuumStore.ROLE );

        Iterator it = store.getAllProjects();

        assertFalse( it.hasNext() );

        int exitCode = cli.work( args );

        assertEquals( 0, exitCode );

        it = store.getAllProjects();

        assertTrue( it.hasNext() );

        ContinuumProject project = (ContinuumProject) it.next();

        assertEquals( "Foo Project", project.getName() );

        assertEquals( "scm:cvs:local:ignored:/cvsroot/:cvs-module", project.getScmConnection() );

        assertEquals( "maven2", project.getType() );

        assertFalse( it.hasNext() );
    }
}
