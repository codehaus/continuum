package org.codehaus.continuum.it.web;

/*
 * LICENSE
 */

import org.codehaus.continuum.AbstractContinuumTest;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;

import org.mortbay.http.SocketListener;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.WebApplicationContext;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AbstractContinuumWebTest.java,v 1.1 2004-10-06 14:27:04 trygvis Exp $
 */
public abstract class AbstractContinuumWebTest
    extends AbstractContinuumTest
{
    private Server jetty;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void setUpJetty( PlexusContainer container )
        throws Exception
    {
        jetty = new Server();

        WebApplicationContext context = new WebApplicationContext();
        context.setContextPath( "/continuum/*" );
        context.setWAR( getTestPath( "src/main/resources/web/continuumweb" ) );
        jetty.addContext(context);
        context.setAttribute( PlexusConstants.PLEXUS_KEY, container );

        SocketListener listener = new SocketListener();

        listener.setHost( "localhost" );
        listener.setPort(9999);
        listener.setMinThreads(5);
        listener.setMaxThreads(250);
        jetty.addListener(listener);

        jetty.start();
    }

    public void tearDownJetty()
        throws Exception
    {
        jetty.stop();
    }
}
