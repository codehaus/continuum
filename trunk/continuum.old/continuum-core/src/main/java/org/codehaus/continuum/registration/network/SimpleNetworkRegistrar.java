package org.codehaus.plexus.continuum.registration.network;

import org.codehaus.plexus.continuum.registration.AbstractContinuumRegistrar;
import org.codehaus.plexus.continuum.trigger.network.DefaultServerSocketFactory;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This trigger listens on a specified port and takes one line
 * of input which contains the the groupId and artifactId of the
 * project to build or the special word "all" to indicate building
 * all the projects.
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: SimpleNetworkRegistrar.java,v 1.1 2004-01-18 18:01:40 jvanzyl Exp $
 */
public class SimpleNetworkRegistrar
    extends AbstractContinuumRegistrar
    implements Initializable, Startable
{
    private int port;

    private Thread serverThread;

    private boolean serverStarted;

    private ServerSocket serverSocket;

    private DefaultServerSocketFactory serverSocketFactory;

    private InetAddress localAddress;

    private InetAddress bindAddress;

    public boolean isServerStarted()
    {
        return serverStarted;
    }

    private void setServerStarted( boolean serverStarted )
    {
        this.serverStarted = serverStarted;
    }

    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    /** @see Initializable#initialize */
    public void initialize()
        throws Exception
    {
        serverSocketFactory = new DefaultServerSocketFactory();

        InetAddress[] adds = InetAddress.getAllByName( "192.168.1.103" );

        bindAddress = adds[0];

        serverSocket = serverSocketFactory.createServerSocket( port, 50 );

        //serverSocket = serverSocketFactory.createServerSocket( port, 50, bindAddress );

        localAddress = InetAddress.getLocalHost();
    }

    public void start()
    {
        if ( serverThread != null )
        {
            return;
        }

        setServerStarted( true );

        serverThread = new Thread( new Runnable()
        {
            public void run()
            {
                while ( isServerStarted() )
                {
                    try
                    {
                        Socket socket = serverSocket.accept();

                        BufferedReader reader = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );

                        String instruction = reader.readLine().trim();

                        getContinuum().addProject( instruction );
                    }
                    catch ( Exception e )
                    {
                        getLogger().error( "Error processing request: ", e );
                    }
                }
            }
        } );

        serverThread.start();
    }

    public void stop()
    {
        setServerStarted( false );

        serverThread = null;

        try
        {
            serverSocket.close();
        }
        catch ( IOException e )
        {
            getLogger().error( "Error shutting down server." );
        }
    }

    public void dispose()
    {
        stop();
    }
}
