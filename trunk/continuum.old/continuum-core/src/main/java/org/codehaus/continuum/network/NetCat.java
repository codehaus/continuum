package org.codehaus.continuum.network;

/*
 * LICENSE
 */

import java.io.ByteArrayOutputStream;
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
 * @version $Id: NetCat.java,v 1.5 2004-07-27 00:06:04 trygvis Exp $
 */
public class NetCat
{
    private static int defaultBufferSize = 4096;

    private NetCat()
    {
    }

    public static String write( String host, int port, String contents )
        throws IOException
    {
        return write( host, port, new StringInputStream( contents ) );
    }

    public static String write( String host, int port, InputStream contents )
        throws IOException
    {
        OutputStream output = null;
        InputStream input;
        Socket socket = null;
        final byte[] buffer = new byte[ defaultBufferSize ];
        int n = 0;
        ByteArrayOutputStream result = new ByteArrayOutputStream();

        try
        {
            socket = new Socket( host, port );
            output = socket.getOutputStream();
            input = socket.getInputStream();

            while ( ( n = contents.read( buffer ) ) != -1 )
            {
//                for( int i = 0; i < n; i++)
//                    System.err.print( (char)buffer[i] );
                output.write( buffer, 0, n );
            }

            System.err.println( "Reading return value" );

            while ( ( n = input.read( buffer ) ) != -1  )
            {
//                for( int i = 0; i < n; i++)
//                    System.err.print( (char)buffer[i] );
                result.write( buffer, 0, n );
            }
        }
        finally
        {
            IOUtil.close( output );

            NetworkUtils.closeSocket( socket );
        }

        return result.toString();
    }
}
