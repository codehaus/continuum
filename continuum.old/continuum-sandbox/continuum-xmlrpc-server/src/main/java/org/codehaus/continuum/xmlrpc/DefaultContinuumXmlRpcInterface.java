package org.codehaus.continuum.xmlrpc;

/*
 * LICENSE
 */

import org.codehaus.continuum.Continuum;
import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.utils.PlexusUtils;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultContinuumXmlRpcInterface.java,v 1.2 2004-07-07 02:34:42 trygvis Exp $
 */
public class DefaultContinuumXmlRpcInterface
    implements ContinuumXmlRpcInterface, Initializable
{
    private Continuum continuum;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws Exception
    {
        PlexusUtils.assertRequirement( continuum, Continuum.ROLE );
    }

    // ----------------------------------------------------------------------
    // Continuum Implementation
    // ----------------------------------------------------------------------

    public String addProject( String name, String scmConnection, String type )
        throws ContinuumException
    {
        return continuum.addProject( name, scmConnection, type );
    }

    public String buildProject( String id )
        throws ContinuumException
    {
        return continuum.buildProject( id );
    }

    public int getBuildQueueLength()
        throws ContinuumException
    {
        return continuum.getBuildQueueLength();
    }

    // ----------------------------------------------------------------------
    // ContinuumXmlRpcInterface Implementation
    // ----------------------------------------------------------------------
/*
    public String registerProject( String name, String scmConnection, String type )
    {
        try
        {
            continuum.addProject( name, scmConnection, type );

            return "OK";
        }
        catch( Exception ex )
        {
            return "FAILURE";
        }
    }
*/
}
