package org.codehaus.continuum.network;

/*
 * LICENSE
 */

import java.io.IOException;
import java.net.Socket;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: NetworkUtils.java,v 1.4 2004-07-01 15:30:57 trygvis Exp $
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
}
