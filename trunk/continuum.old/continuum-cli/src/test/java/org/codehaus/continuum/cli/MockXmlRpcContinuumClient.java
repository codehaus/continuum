package org.codehaus.continuum.cli;

/*
 * LICENSE
 */

import org.codehaus.continuum.Continuum;
import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.xmlrpc.XmlRpcContinuumClient;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: MockXmlRpcContinuumClient.java,v 1.1 2004-07-07 05:05:36 trygvis Exp $
 */
public class MockXmlRpcContinuumClient
    implements XmlRpcContinuumClient
{
    private Continuum continuum;

    public void setHostname( String hostname )
    {
    }

    public void setPort( String port )
    {
    }

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
