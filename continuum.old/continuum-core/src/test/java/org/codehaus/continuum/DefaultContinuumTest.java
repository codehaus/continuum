package org.codehaus.plexus.continuum;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.continuum.buildqueue.BuildQueue;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultContinuumTest.java,v 1.12 2004-04-24 23:54:13 trygvis Exp $
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

        Thread.sleep( 10000 );

        assertEquals( 1, queue.getLength() );
    }
}
