package org.codehaus.continuum.xmlrpc;

/*
 * Copyright (c) 2004, Jason van Zyl and Trygve Laugstøl
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcClient;

import org.codehaus.continuum.ContinuumException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultXmlRpcContinuumClient.java,v 1.2 2004-07-29 04:20:33 trygvis Exp $
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
