package org.codehaus.plexus.continuum.scm.cvsimpl;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.continuum.scm.Scm;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * 
 * @author <a href="mailto:jason@plexus.org">Jason van Zyl</a>
 *
 * @version $Id: CvsScmTest.java,v 1.3 2003-10-12 01:14:56 pdonald Exp $
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

        parameters.put( "cvsroot", ":pserver:anonymous@cvsimpl.codehaus.org:/scm/cvspublic" );
        parameters.put( "module", "classworlds" );

        scm.checkout( parameters );

        assertNotNull( scm );
    }
}
