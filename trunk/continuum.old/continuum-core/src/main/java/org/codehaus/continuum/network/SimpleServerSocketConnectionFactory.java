package org.codehaus.continuum.network;

/*
 * LICENSE
 */

import java.io.IOException;
import java.net.ServerSocket;

import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: SimpleServerSocketConnectionFactory.java,v 1.4 2004-07-01 15:30:57 trygvis Exp $
 */
public class SimpleServerSocketConnectionFactory
    extends AbstractLogEnabled
    implements ConnectionFactory, Initializable, Startable
{
    /** @default 0 */
    private int port;

    /** @default 50 */
    private int backlog;

    private ConnectionConsumer consumer;

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

        ServerSocket serverSocket;

        try
        {
            serverSocket = new ServerSocket( port, backlog );
        }
        catch( IOException ex)
        {
            throw new Exception( "Could not create a server socket.", ex );
        }

        thread = new WorkerThread( serverSocket, consumer, getLogger() );

        thread.start();

        getLogger().info( "Started socket listener on port " + port );
    }

    public void stop()
    {
        getLogger().info( "Stopping socket listener on port " + port );

        running = false;

        if ( thread.getServerSocket() != null )
        {
            try
            {
                thread.getServerSocket().close();
            }
            catch( IOException ex )
            {
                // ignore
            }
        }

        getLogger().info( "Stopped socket listener on port " + port );
    }

    public boolean isRunning()
    {
        return running;
    }
}
