package org.codehaus.continuum;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.continuum.buildqueue.BuildQueue;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultContinuumTest.java,v 1.14 2004-05-13 17:48:17 trygvis Exp $
 */
public class DefaultContinuumTest
    extends PlexusTestCase
{
    private String project1Url = "http://cvs.plexus.codehaus.org/viewcvs.cgi/*checkout*/plexus/plexus-components/native/continuum/src/test-projects/project1/project.xml?root=codehaus";

    public void testContinuum()
        throws Exception
    {
        Continuum continuum = (Continuum) lookup( Continuum.ROLE );

        BuildQueue queue = (BuildQueue) lookup( BuildQueue.ROLE );

        assertNotNull( continuum );

        continuum.addProject( project1Url );

        continuum.buildProject( "plexus", "continuum-project1" );

        // NOTE: this test might fail if you have a slow system
        // because the builder thread might start before the return of the call.
        assertEquals( 1, queue.getLength() );

        Thread.sleep( 10000 );
    }
}
