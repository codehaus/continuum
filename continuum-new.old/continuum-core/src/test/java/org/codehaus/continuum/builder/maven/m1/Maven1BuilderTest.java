package org.codehaus.continuum.builder.maven.m1;

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

import org.codehaus.continuum.builder.manager.BuilderManager;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: Maven1BuilderTest.java,v 1.2 2005-03-09 00:15:20 trygvis Exp $
 */
public class Maven1BuilderTest
    extends PlexusTestCase
{
    public void testBasic()
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

        assertEquals( "clean:clean,jar:install", configuration.getProperty( Maven1Builder.CONFIGURATION_GOALS ) );
    }
}
