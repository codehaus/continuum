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

import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.store.ContinuumStore;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.xmlrpc.XmlRpcComponent;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultXmlRpcContinuumClientTest.java,v 1.2 2004-07-29 04:20:33 trygvis Exp $
 */
public class DefaultXmlRpcContinuumClientTest
    extends PlexusTestCase
{
    private URL url;

    private XmlRpcContinuumClient client;

    private ContinuumStore store;

    public void setUp()
        throws Exception
    {
        super.setUp();

        // start the server
        assertNotNull( lookup( XmlRpcComponent.ROLE ) );

        client = (XmlRpcContinuumClient) lookup( XmlRpcContinuumClient.ROLE );

        store = (ContinuumStore) lookup( ContinuumStore.ROLE );

        assertNotNull( client );

        assertNotNull( store );
    }

    public void testAddProject()
        throws Exception
    {
        String projectName = "Foo Project";

        String scmConnection = "scm:cvs:local:ignored:/cvsroot:cvs-module";

        String type = "maven2";

        String id = client.addProject( projectName, scmConnection, type );

        ContinuumProject project = store.getProject( id );

        assertNotNull( project );

        assertEquals( projectName, project.getName() );

        assertEquals( scmConnection, project.getScmConnection() );

        assertEquals( type, project.getType() );
    }
}
