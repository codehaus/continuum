package org.codehaus.continuum.builder;

/*
 * LICENSE
 */

import org.codehaus.continuum.AbstractContinuumTest;
import org.codehaus.continuum.Continuum;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumProjectState;
import org.codehaus.continuum.store.ContinuumStore;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AbstractBuilderTest.java,v 1.1 2004-07-27 00:06:10 trygvis Exp $
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

        String scm = "scm:test:" + 
                     getTestFile( "src/test/projects/" + getProjectType()) + 
                     ":normal";

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
