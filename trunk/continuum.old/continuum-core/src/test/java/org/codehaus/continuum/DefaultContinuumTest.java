package org.codehaus.plexus.continuum;

import org.codehaus.plexus.PlexusTestCase;

import java.io.InputStream;

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

    protected InputStream getCustomConfiguration()
    {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        InputStream is = cl.getResourceAsStream( "org/codehaus/plexus/continuum/plexus.xml" );

        return is;
    }

    public void testContinuum()
        throws Exception
    {
        Continuum continuum = (Continuum) lookup( Continuum.ROLE );

        assertNotNull( continuum );
    }
}
