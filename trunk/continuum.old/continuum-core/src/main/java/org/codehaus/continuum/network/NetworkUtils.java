package org.codehaus.continuum.network;

/*
 * LICENSE
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: NetworkUtils.java,v 1.3 2004-05-13 17:48:17 trygvis Exp $
 */
public class NetworkUtils
{
    private NetworkUtils()
    {
    }

    static public void closeSocket( Socket socket )
    {
        try
        {
            if ( socket != null )
                socket.close();
        }
        catch( IOException ex )
        {
            // ignore
        }
    }

    static public void closeInput( InputStream input )
    {
        try
        {
            if ( input != null )
                input.close();
        }
        catch( IOException ex )
        {
            // ignore
        }
    }

    static public void closeOutput( OutputStream output )
    {
        try
        {
            if ( output != null )
                output.close();
        }
        catch( IOException ex )
        {
            // ignore
        }
    }
}
