package org.codehaus.continuum.network;

/*
 * LICENSE
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: SimpleServerSocketConnectionFactory.java,v 1.3 2004-05-13 17:48:17 trygvis Exp $
 */
public class SimpleServerSocketConnectionFactory
    extends AbstractLogEnabled
    implements ConnectionFactory, Initializable, Startable
{
    // configuration
    private int port;

    private int backlog;

    private ConnectionConsumer consumer;

    // member variables
    private ServerSocket serverSocket;

    private boolean running;

    private WorkerThread thread;

    ///////////////////////////////////////////////////////////////////////////
    // Component Lifecycle
    
    public void initialize()
        throws Exception
    {
        getLogger().info( "Initializing socket listener." );

        if( port <= 0 )
            throw new PlexusConfigurationException( "The port must be bigger than 0." );

        if( port >= 65536 )
            throw new PlexusConfigurationException( "The port must be lesser than 65536." );

        if( backlog < 0 )
            throw new PlexusConfigurationException( "The valud of the backlog element must be bigger than 0." );

        if( consumer == null )
            throw new PlexusConfigurationException( "There is no connection consumer configured." );

        getLogger().info( "Initialized socket listener." );
    }

    public void start()
        throws Exception
    {
        getLogger().info( "Starting socket listener on port " + port );

        try
        {
            serverSocket = new ServerSocket( port, backlog );
        }
        catch( IOException ex)
        {
            throw new Exception( "Could not create a server socket.", ex );
        }

        running = true;

        thread = new WorkerThread();
        thread.start();

        getLogger().info( "Started socket listener on port " + port );
    }

    public void stop()
    {
        getLogger().info( "Stopping socket listener on port " + port );

        running = false;

        if ( serverSocket != null )
        {
            try
            {
                serverSocket.close();
            }
            catch( IOException ex )
            {
                // ignore
            }
        }

        getLogger().info( "Stopped socket listener on port " + port );
    }

    ///////////////////////////////////////////////////////////////////////////
    // InputStreamFactory implementation

    private class WorkerThread extends Thread
    {
        public void run()
        {
//            getLogger().info( "Worker thread for port " + port + " is running" );
            while( isRunning() )
            {
                Socket socket;
                InputStream input;
                OutputStream output;

                try
                {
                    socket = serverSocket.accept();
                }
                catch( IOException ex)
                {
                    if ( isRunning() )
                        getLogger().warn( "Exception while accepting socket.", ex );

                    return;
                }

//                getLogger().info( "Got connection from: " + socket.getInetAddress() );

                try
                {
                    input = socket.getInputStream();
                    output = socket.getOutputStream();
                }
                catch( IOException ex )
                {
                    getLogger().fatalError( "Exception while getting the input and output streams from the socket.", ex );
                    continue;
                }

                try
                {
                    consumer.consumeConnection( input, output );
                }
                catch( IOException ex )
                {
                    getLogger().fatalError( "Exception while consuming connection.", ex );
                }

                NetworkUtils.closeInput( input );
                NetworkUtils.closeOutput( output );
                NetworkUtils.closeSocket( socket );
            }

//            getLogger().info( "Worker thread for port " + port + " exiting." );
        }
    }

    public boolean isRunning()
    {
        return running;
    }
}
