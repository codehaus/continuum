package org.codehaus.continuum;

import java.io.File;

import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;

import org.codehaus.continuum.buildqueue.BuildQueue;
import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultContinuumTest.java,v 1.16 2004-06-27 23:21:03 trygvis Exp $
 */
public class DefaultContinuumTest
    extends PlexusTestCase
{
//  private String project1Url = "http://cvs.plexus.codehaus.org/viewcvs.cgi/*checkout*/plexus/plexus-components/native/continuum/src/test-projects/project1/project.xml";

    private String project1Url = getTestFile( "." ) + "/src/test-projects/project1/pom.xml";

    public void testContinuum()
        throws Exception
    {
        Continuum continuum = (Continuum) lookup( Continuum.ROLE );

        MavenProjectBuilder projectBuilder = (MavenProjectBuilder) lookup( MavenProjectBuilder.ROLE );

        BuildQueue queue = (BuildQueue) lookup( BuildQueue.ROLE );

        assertNotNull( continuum );

        MavenProject project = projectBuilder.build( new File( project1Url ) );

        String connection = project.getScm().getConnection();

        System.out.println( "SCM Connection:" + connection );

        String repoRoot = getTestFile( "src/test/repository/" );

        int index = connection.indexOf( "/cvs/root" );

        connection = connection.substring( 0, index ) + repoRoot + ":" + connection.substring( index + 10 );

        System.err.println( "connection: " + connection );

        project.getScm().setConnection( connection );

        String projectId = continuum.addProject( project );

        String jobId = continuum.buildProject( projectId );

        jobId = jobId.toString();

        // NOTE: this test might fail if you have a slow system
        // because the builder thread might start before the return of the call.
        assertEquals( 1, queue.getLength() );

        Thread.sleep( 10000 );

        assertEquals( 0, queue.getLength() );

        release( continuum );
    }
}
