package org.codehaus.plexus.continuum;

import org.codehaus.plexus.PlexusTestCase;

public class DefaultContinuumTest
    extends PlexusTestCase
{
    public DefaultContinuumTest( String s )
    {
        super( s );
    }

    public void setUp()
        throws Exception
    {
        super.setUp();
    }

    public void testContinuum()
        throws Exception
    {
        Continuum continuum = (Continuum) lookup( Continuum.ROLE );

        assertNotNull( continuum );

        while( true ){}
    }
}
