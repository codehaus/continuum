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

import java.net.URL;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcClient;

import org.codehaus.continuum.store.ContinuumStore;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.xmlrpc.XmlRpcComponent;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultContinuumXmlRpcServerTest.java,v 1.3 2004-07-29 04:20:57 trygvis Exp $
 */
public class DefaultContinuumXmlRpcServerTest
    extends PlexusTestCase
{
    private URL url;

    private ContinuumStore continuumStore;

    public void setUp()
        throws Exception
    {
        super.setUp();

        url = new URL( "http://localhost:8080/rpc2" );

        // start the server
        assertNotNull( lookup( XmlRpcComponent.ROLE ) );

        continuumStore = (ContinuumStore) lookup( ContinuumStore.ROLE );
    }

    public void testBasic()
        throws Exception
    {
        String projectName = "Test Project";

        Vector arguments = new Vector();

        arguments.add( projectName );

        arguments.add( "scm:cvs:local:ignored:/cvsroot:cvs-module" );

        arguments.add( "maven2" );
        // TODO: Renable the assertions when maven is embeddable
/*
        String id = execute( "addProject", arguments ).toString();

        ContinuumProject project = continuumStore.getProject( id );

        assertNotNull( project );

        assertEquals( projectName, project.getName() );
*/
    }

    public Object execute( String method, Vector arguments )
        throws Exception
    {
        XmlRpcClient client = new XmlRpcClient( url );

        return client.execute( "continuum." + method, arguments );
    }
}
