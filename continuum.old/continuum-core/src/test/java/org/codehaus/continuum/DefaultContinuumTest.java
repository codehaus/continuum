package org.codehaus.plexus.continuum;

import org.codehaus.plexus.PlexusTestCase;

/**
 *
 *
 * @author <a href="mailto:jason@plexus.org">Jason van Zyl</a>
 *
 * @version $Id: DefaultContinuumTest.java,v 1.2 2003-10-12 00:55:40 pdonald Exp $
 */
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
        Continuum continuum = (Continuum) lookup( Continuum.class.getName() );

        assertNotNull( continuum );
    }
}
