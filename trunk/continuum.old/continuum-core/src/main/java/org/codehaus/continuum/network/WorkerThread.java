package org.codehaus.continuum.network;

/*
 * LICENSE
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.IOUtil;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l </a>
 * @version $Id: WorkerThread.java,v 1.2 2004-07-02 03:27:07 trygvis Exp $
 */
public class WorkerThread
    extends Thread
{
    private boolean running;

    private ServerSocket serverSocket;

    private ConnectionConsumer connectionConsumer;

    private Logger logger;

    public WorkerThread( ServerSocket serverSocket, ConnectionConsumer connectionConsumer, Logger logger )
    {
        this.serverSocket = serverSocket;

        this.connectionConsumer = connectionConsumer;

        this.logger = logger;
    }

    public void run()
    {
        running = true;

        while ( running )
        {
            Socket socket;

            try
            {
                socket = serverSocket.accept();
            }
            catch ( IOException ex )
            {
                if ( running )
                    getLogger().warn( "Exception while accepting socket.", ex );

                return;
            }

            //                getLogger().info( "Got connection from: " +
            // socket.getInetAddress() );

            InputStream input;

            OutputStream output;

            try
            {
                input = socket.getInputStream();

                output = socket.getOutputStream();
            }
            catch ( IOException ex )
            {
                getLogger().fatalError( "Exception while getting the input and output streams from the socket.", ex );
                continue;
            }

            try
            {
                connectionConsumer.consumeConnection( input, output );
            }
            catch ( IOException ex )
            {
                getLogger().fatalError( "Exception while consuming connection.", ex );
            }

            IOUtil.close( input );

            IOUtil.close( output );

            NetworkUtils.closeSocket( socket );
        }

//        getLogger().info( "Worker thread for port " + port + " exiting." );
    }

    public void shutdown()
    {
        running = false;
    }

    public ServerSocket getServerSocket()
    {
        return serverSocket;
    }

    private Logger getLogger()
    {
        return logger;
    }
}
