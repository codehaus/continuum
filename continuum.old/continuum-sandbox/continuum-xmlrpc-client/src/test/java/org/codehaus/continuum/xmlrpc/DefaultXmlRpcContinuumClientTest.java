package org.codehaus.continuum.xmlrpc;

/*
 * LICENSE
 */

import java.net.URL;

import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.store.ContinuumStore;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.xmlrpc.XmlRpcComponent;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultXmlRpcContinuumClientTest.java,v 1.1 2004-07-07 04:56:41 trygvis Exp $
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
