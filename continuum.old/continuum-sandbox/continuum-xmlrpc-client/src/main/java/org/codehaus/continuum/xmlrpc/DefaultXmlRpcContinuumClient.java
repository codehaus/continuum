package org.codehaus.continuum.xmlrpc;

/*
 * LICENSE
 */

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcClient;

import org.codehaus.continuum.ContinuumException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultXmlRpcContinuumClient.java,v 1.1 2004-07-07 04:56:40 trygvis Exp $
 */
public class DefaultXmlRpcContinuumClient
    implements XmlRpcContinuumClient
{
    private String hostname;

    private String port;

    // ----------------------------------------------------------------------
    // Continuum Implementation
    // ----------------------------------------------------------------------

    public String addProject( String name, String scmConnection, String type )
        throws ContinuumException
    {
        Vector arguments = new Vector();

        arguments.add( name );

        arguments.add( scmConnection );

        arguments.add( type );

        Object object = execute( "addProject", arguments );

        if ( !( object instanceof String ) )
        {
            throw new ContinuumException( "Wrong return type from call. Expected java.lang.String, got: " + object.getClass().getName() );
        }

        return object.toString();
    }

    public String buildProject( String id )
        throws ContinuumException
    {
        throw new ContinuumException( "NOT IMPLEMENTED" );
    }

    public int getBuildQueueLength()
        throws ContinuumException
    {
        throw new ContinuumException( "NOT IMPLEMENTED" );
    }

    // ----------------------------------------------------------------------
    // XmlRpcContinuum Implementation
    // ----------------------------------------------------------------------

    public void setHostname( String hostname )
    {
        this.hostname = hostname;
    }

    public void setPort( String port )
    {
        this.port = port;
    }

    // ----------------------------------------------------------------------
    // Private
    // ----------------------------------------------------------------------

    private Object execute( String method, Vector arguments )
        throws ContinuumException
    {
        if ( hostname == null )
        {
            hostname = "localhost";
        }

        if ( port == null )
        {
            port = "8080";
        }

        URL url;

        try
        {
            url = new URL( "http://" + hostname + ":" + port + "/rpc2" );
        }
        catch( MalformedURLException ex )
        {
            throw new ContinuumException( "Error parsing the XML-RPC url.", ex );
        }

        try
        {
            XmlRpcClient client = new XmlRpcClient( url );

            return client.execute( "continuum." + method, arguments );
        }
        catch( Exception ex )
        {
            throw new ContinuumException( "Remoting error.", ex );
        }
    }
}
