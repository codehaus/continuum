package org.codehaus.continuum.maven;

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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.maven.project.MavenProject;

import org.codehaus.plexus.DefaultArtifactEnabledContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: MavenToolTest.java,v 1.2 2004-10-28 18:27:04 trygvis Exp $
 */
public class MavenToolTest
    extends TestCase
{
    private MavenTool mavenTool;

    public void setUp()
    	throws Exception
    {
        String mavenHome = System.getProperty( "maven.home" );
        String mavenHomeLocal = System.getProperty( "maven.home" );
        String mavenRepo = System.getProperty( "maven.repo" );

        if ( mavenHome == null )
        {
            File tmp = new File( System.getProperty( "user.home" ), "m2" );

            assertTrue( "Could not find maven.home in '" + tmp.getAbsolutePath() + "'.", tmp.isDirectory() );

            mavenHome = tmp.getAbsolutePath();
        }

        if ( mavenHomeLocal == null )
        {
            File tmp = new File( System.getProperty( "user.home" ), ".m2" );

            assertTrue( "Could not find maven.home.local in '" + tmp.getAbsolutePath() + "'.", tmp.isDirectory() );

            mavenHomeLocal = tmp.getAbsolutePath();
        }

        PlexusContainer container = new DefaultArtifactEnabledContainer();

        container.getContext().put( "maven.home", mavenHome );
        container.getContext().put( "maven.home.local", mavenHomeLocal );

        if ( mavenRepo != null )
        {
            container.getContext().put( "maven.repo", mavenRepo );
        }

        container.initialize();
        container.start();

        mavenTool = (MavenTool) container.lookup( MavenTool.ROLE );
    }

    public void testPomXmlLoading()
        throws Exception
    {
        MavenProject project = mavenTool.getProject( PlexusTestCase.getTestFile( "src/test/resources/maven-tool-pom.xml" ) );

        assertEquals( "continuum-test", project.getGroupId() );

        assertEquals( "maven-tool-test", project.getArtifactId() );

        assertEquals( "1.0", project.getVersion() );

        assertEquals( "Maven Tool Test Project", project.getName() );
    }

    public void testExecuteExternal()
    	throws Exception
    {
        List goals = new ArrayList();

        goals.add( "clean:clean" );

        File workingDirectory = PlexusTestCase.getTestFile( "src/test/repository/external-maven" );

        MavenProject mavenProject = mavenTool.getProject( new File( workingDirectory, "pom.xml" ) );

        mavenTool.executeExternal( workingDirectory, mavenProject, goals );
    }
}
