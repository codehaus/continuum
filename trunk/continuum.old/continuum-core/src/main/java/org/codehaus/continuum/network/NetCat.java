package org.codehaus.plexus.continuum.network;

/*
 * LISENCE
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringInputStream;

/**
 * This class emulates the unix tool <code>nc</code>.
 * 
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: NetCat.java,v 1.1 2004-04-07 15:56:55 trygvis Exp $
 */
public class NetCat
{
    private static int defaultBufferSize = 4096;

    private NetCat()
    {
    }

    public static void write( String host, int port, String contents )
        throws IOException
    {
        write( host, port, new StringInputStream( contents ) );
    }

    public static void write( String host, int port, InputStream contents )
        throws IOException
    {
        OutputStream output = null;
        Socket socket = null;
        byte[] buffer = new byte[ defaultBufferSize ];

        try
        {
            socket = new Socket( host, port );
            output = socket.getOutputStream();

            IOUtil.copy( contents, output );
        }
        finally
        {
            NetworkUtils.closeOutput( output );
            NetworkUtils.closeSocket( socket );
        }
    }
}
