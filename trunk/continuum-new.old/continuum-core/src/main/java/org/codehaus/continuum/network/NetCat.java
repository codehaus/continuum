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

import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringInputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * This class emulates the unix tool <code>nc</code>.
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: NetCat.java,v 1.1.1.1 2005-02-17 22:23:50 trygvis Exp $
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
        final byte[] buffer = new byte[defaultBufferSize];
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

            while ( ( n = input.read( buffer ) ) != -1 )
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
