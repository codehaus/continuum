package org.apache.maven.continuum.builder.maven.m1;

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
import java.util.Properties;

import org.apache.maven.continuum.builder.manager.BuilderManager;
import org.apache.maven.continuum.project.ContinuumProject;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.FileUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: Maven1BuilderTest.java,v 1.3 2005-04-06 13:57:20 trygvis Exp $
 */
public class Maven1BuilderTest
    extends PlexusTestCase
{
    public void testBuildingAProjectFromMetadataWithACompleteMaven1Pom()
        throws Exception
    {
        BuilderManager builderManager = (BuilderManager) lookup( BuilderManager.ROLE );

        Maven1Builder builder = (Maven1Builder) builderManager.getBuilder( "maven-1" );

        ContinuumProject project = builder.createProjectFromMetadata( getTestFile( "src/test/resources/projects/maven-1.pom.xml" ).toURL() );

        assertNotNull( project );

        assertEquals( "Maven", project.getName() );

        assertEquals( "scm:svn:http://svn.apache.org/repos/asf:maven/maven-1/core/trunk/", project.getScmUrl() );

        assertEquals( "dev@maven.apache.org", project.getNagEmailAddress() );

        assertEquals( "1.1-SNAPSHOT", project.getVersion() );

        Properties configuration = project.getConfiguration();

        assertNotNull( configuration );

        assertEquals( 1, configuration.size() );

        assertEquals( "clean:clean, jar:install", configuration.getProperty( Maven1Builder.CONFIGURATION_GOALS ) );
    }

    public void testUpdatingAProjectFromScmWithAExistingProjectAndAEmptyMaven1Pom()
        throws Exception
    {
        BuilderManager builderManager = (BuilderManager) lookup( BuilderManager.ROLE );

        Maven1Builder builder = (Maven1Builder) builderManager.getBuilder( "maven-1" );

        // ----------------------------------------------------------------------
        // Make a checkout
        // ----------------------------------------------------------------------

        File checkOut = getTestFile( "target/test-checkout" );

        if ( !checkOut.exists() )
        {
            assertTrue( checkOut.mkdirs() );
        }

        FileUtils.cleanDirectory( checkOut );

        FileUtils.fileWrite( new File( checkOut, "project.xml" ).getAbsolutePath(), "<project/>" );

        // ----------------------------------------------------------------------
        // Make the "existing" project
        // ----------------------------------------------------------------------

        ContinuumProject project = new ContinuumProject();

        project.setName( "Maven" );

        project.setScmUrl( "scm:svn:http://svn.apache.org/repos/asf:maven/maven-1/core/trunk/" );

        project.setNagEmailAddress( "dev@maven.apache.org" );

        project.setVersion( "1.1-SNAPSHOT" );

        Properties expectedConfiguration = new Properties();

        expectedConfiguration.put( Maven1Builder.CONFIGURATION_GOALS, "clean:clean, jar:install" );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        builder.updateProjectFromCheckOut( checkOut, project );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        assertNotNull( project );

        assertEquals( "Maven", project.getName() );

        assertEquals( "scm:svn:http://svn.apache.org/repos/asf:maven/maven-1/core/trunk/", project.getScmUrl() );

        assertEquals( "dev@maven.apache.org", project.getNagEmailAddress() );

        assertEquals( "1.1-SNAPSHOT", project.getVersion() );

        Properties configuration = project.getConfiguration();

        assertNotNull( configuration );

        assertEquals( 1, configuration.size() );

        assertEquals( "clean:clean, jar:install", configuration.getProperty( Maven1Builder.CONFIGURATION_GOALS ) );
    }
}
