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
 * @version $Id: DefaultContinuumXmlRpcServer.java,v 1.1 2004-07-07 03:21:48 trygvis Exp $
 */
public class DefaultContinuumXmlRpcServer
    implements ContinuumXmlRpcServer, Initializable
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
}
