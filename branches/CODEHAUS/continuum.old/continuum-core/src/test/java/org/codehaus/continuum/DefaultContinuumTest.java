package org.codehaus.plexus.continuum;

import org.codehaus.plexus.PlexusTestCase;

/**
 *
 * 
 * @author <a href="mailto:jason@plexus.org">Jason van Zyl</a>
 *
 * @version $Id: DefaultContinuumTest.java,v 1.1.1.1 2003-09-01 16:06:05 jvanzyl Exp $
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
        Continuum continuum = (Continuum) lookup( Continuum.ROLE );

        assertNotNull( continuum );
    }
}
