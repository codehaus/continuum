package org.codehaus.continuum;

/*
 * LICENSE
 */

import org.codehaus.continuum.buildqueue.BuildQueue;
import org.codehaus.continuum.project.BuildResult;
import org.codehaus.continuum.store.ContinuumStore;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultContinuumTest.java,v 1.19 2004-07-03 03:21:17 trygvis Exp $
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

        String projectId = continuum.addProject( "Continuum Test Project 1", repo );

        assertEquals( 0, queue.getLength() );

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
