package org.codehaus.plexus.continuum.scm.cvs;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.continuum.scm.Scm;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * 
 * @author <a href="mailto:jason@plexus.org">Jason van Zyl</a>
 *
 * @version $Id: CvsScmTest.java,v 1.1.1.1 2003-09-01 16:06:05 jvanzyl Exp $
 */
public class CvsScmTest
    extends PlexusTestCase
{
    public CvsScmTest( String s )
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
        Scm scm = (Scm) lookup( Scm.ROLE );

        Map parameters = new HashMap();

        parameters.put( "cvsroot", ":pserver:anonymous@cvs.codehaus.org:/scm/cvspublic" );
        parameters.put( "module", "classworlds" );

        scm.checkout( parameters );

        assertNotNull( scm );
    }
}
