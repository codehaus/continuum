package org.codehaus.continuum;

/*
 * LICENSE
 */

import java.io.File;

import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;

import org.codehaus.continuum.buildqueue.BuildQueue;
import org.codehaus.continuum.project.BuildResult;
import org.codehaus.continuum.store.ContinuumStore;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultContinuumTest.java,v 1.18 2004-07-01 20:53:30 trygvis Exp $
 */
public class DefaultContinuumTest
    extends AbstractContinuumTest
{
//  private String project1Url = "http://cvs.plexus.codehaus.org/viewcvs.cgi/*checkout*/plexus/plexus-components/native/continuum/src/test-projects/project1/project.xml";

    private String project1Url = getTestFile( "." ) + "/src/test-projects/project1/pom.xml";

    public void testContinuum()
        throws Exception
    {
        Continuum continuum = getContinuum();

        MavenProjectBuilder projectBuilder = getMavenProjectBuilder();

        BuildQueue queue = getBuildQueue();

        ContinuumStore store = getContinuumStore();

        MavenProject project = projectBuilder.build( new File( project1Url ), getLocalRepository() );

        String connection = project.getScm().getConnection();

        String repoRoot = getTestFile( "src/test/repository/" );

        int index = connection.indexOf( "/cvs/root" );

        connection = connection.substring( 0, index ) + repoRoot + ":" + connection.substring( index + 10 );

        project.getScm().setConnection( connection );

        String projectId = continuum.addProject( project );

        String buildId = continuum.buildProject( projectId );

        // NOTE: this test might fail if you have a slow system
        // because the builder thread might start before the return of the call.
        assertEquals( 1, queue.getLength() );

        int time = 30 * 1000;

        int interval = 100;

        BuildResult result = null;

        while( time > 0 )
        {
            Thread.sleep( interval );

            time -= interval;

            result = store.getBuildResult( buildId );

            if ( result.getState() != BuildResult.BUILD_BUILDING )
            {
                break;
            }
        }

        if ( time <= 0 )
        {
            fail( "Timeout while waiting for the build to finnish." );
        }

        assertEquals( BuildResult.buildStateToString( BuildResult.BUILD_RESULT_OK ), 
                      BuildResult.buildStateToString( result.getState() ) );

        assertEquals( 0, queue.getLength() );

        release( continuum );
    }
}
