package org.codehaus.continuum.store;

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

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.continuum.project.ContinuumProject;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ModelloPrevaylerContinuumStoreTest.java,v 1.1 2005-02-21 14:58:11 trygvis Exp $
 */
public class ModelloPrevaylerContinuumStoreTest
    extends PlexusTestCase
{
    public void testBasic()
        throws Exception
    {
        FileUtils.cleanDirectory( getTestPath( "target/plexus-home" ) );

        ContinuumStore store = (ContinuumStore) lookup( ContinuumStore.ROLE, "modello-prevayler" );

        assertEquals( ModelloPrevaylerContinuumStore.class, store.getClass() );

        String name = "Test Project";
        String scmUrl = "scm:local:src/test/repo";
        String nagEmailAddress = "foo@bar.com";
        String version = "1.0";
        String type = "maven2";
        String workingDirectory = "/tmp";
        Properties properties = new Properties();

        String projectId = store.addProject( name, scmUrl, nagEmailAddress, version, type, workingDirectory, properties );

        assertNotNull( projectId );

        ContinuumProject project = store.getProject( projectId );

        assertNotNull( project );

        assertEquals( name, project.getName() );

        assertEquals( scmUrl, project.getScmUrl() );

        assertEquals( nagEmailAddress, project.getNagEmailAddress() );

        assertEquals( version, project.getVersion() );

        assertEquals( type, project.getBuilderId() );

        assertEquals( workingDirectory, project.getWorkingDirectory() );
    }
}
