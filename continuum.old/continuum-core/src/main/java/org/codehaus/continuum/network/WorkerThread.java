package org.codehaus.continuum.network;

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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.IOUtil;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l </a>
 * @version $Id: WorkerThread.java,v 1.3 2004-07-27 05:42:13 trygvis Exp $
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
