package org.codehaus.continuum.xmlrpc;

/*
 * LICENSE
 */

import java.net.URL;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcClient;

import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.store.ContinuumStore;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.xmlrpc.XmlRpcComponent;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultContinuumXmlRpcServerTest.java,v 1.1 2004-07-07 03:21:48 trygvis Exp $
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

        String id = execute( "addProject", arguments ).toString();

        ContinuumProject project = continuumStore.getProject( id );

        assertNotNull( project );

        assertEquals( projectName, project.getName() );
    }

    public Object execute( String method, Vector arguments )
        throws Exception
    {
        XmlRpcClient client = new XmlRpcClient( url );

        return client.execute( "continuum." + method, arguments );
    }
}
