package org.codehaus.continuum.network;

/*
 * The MIT License
 *
 * Copyright (c) 2004, Jason van Zyl and Trygve Laugst�l
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

import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: SimpleServerSocketConnectionFactory.java,v 1.1.1.1 2005-02-17 22:23:50 trygvis Exp $
 */
public class SimpleServerSocketConnectionFactory
    extends AbstractLogEnabled
    implements ConnectionFactory, Initializable, Startable
{
    /**
     * @default 0
     */
    private int port;

    /**
     * @default 50
     */
    private int backlog;

    private ConnectionConsumer consumer;

    private WorkerThread thread;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws Exception
    {
        if ( port <= 0 )
            throw new PlexusConfigurationException( "The port must be bigger than 0." );

        if ( port >= 65536 )
            throw new PlexusConfigurationException( "The port must be lesser than 65536." );

        if ( backlog < 0 )
            throw new PlexusConfigurationException( "The valud of the backlog element must be bigger than 0." );

        if ( consumer == null )
            throw new PlexusConfigurationException( "There is no connection consumer configured." );
    }

    public void start()
        throws Exception
    {
        getLogger().info( "Starting socket listener on port " + port );

        ServerSocket serverSocket;

        try
        {
            serverSocket = new ServerSocket( port, backlog );
        }
        catch ( IOException ex )
        {
            throw new Exception( "Could not create a server socket.", ex );
        }

        thread = new WorkerThread( serverSocket, consumer, getLogger() );

        thread.start();
    }

    public void stop()
    {
        getLogger().info( "Stopping socket listener on port " + port );

        thread.shutdown();

        if ( thread.getServerSocket() != null )
        {
            try
            {
                thread.getServerSocket().close();
            }
            catch ( IOException ex )
            {
                // ignore
            }
        }
    }
}
