package org.codehaus.continuum.store;

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

import java.util.Properties;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.continuum.project.ContinuumProject;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ModelloPrevaylerContinuumStoreTest.java,v 1.2 2005-03-10 00:05:56 trygvis Exp $
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
