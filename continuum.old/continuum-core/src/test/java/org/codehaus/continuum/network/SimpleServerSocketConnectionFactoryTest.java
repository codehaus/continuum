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

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.codehaus.continuum.AbstractContinuumTest;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: SimpleServerSocketConnectionFactoryTest.java,v 1.7 2004-07-27 05:42:15 trygvis Exp $
 */
public class SimpleServerSocketConnectionFactoryTest
    extends AbstractContinuumTest
{
    private int port = 6789;

    private byte[] rawData = {
        (byte)0xca, (byte)0xfe, (byte)0xba, (byte)0xbe,
        (byte)0xde, (byte)0xad, (byte)0xbe, (byte)0xef
    };

    public void testBasic()
        throws Exception
    {
        ConnectionFactory factory;
        Socket socket;
        InputStream input;
        OutputStream output;
        byte[] readData;
        int i, data;

        factory = (ConnectionFactory)lookup( ConnectionFactory.ROLE );

        System.err.println( "Connecting..." );
        socket = new Socket( "127.0.0.1", port );
        System.err.println( "Connected" );

        output = socket.getOutputStream();
        input = socket.getInputStream();

        for( i = 0; i < rawData.length; i++)
        {
            output.write( rawData[i] );
        }

        readData = new byte[ rawData.length ];

        for( i = 0; i < readData.length; i++ )
        {
            data = input.read();

            if( data == -1 )
            {
                fail( "Unexpected end of stream." );
            }

            System.err.println( "Read " + Integer.toHexString( data ) );

            readData[ i ] = (byte)data;
        }

        assertEquals( rawData, readData );

        output.close();
        input.close();

        release( factory );
    }

    private void assertEquals( byte[] expected, byte[] actual )
    {
        int i;

        assertEquals( expected.length, actual.length );

        for( i = 0; i < expected.length; i++ )
        {
            assertEquals( "Checking byte #" + i, expected[ i ], actual[ i ] );
        }
    }
}
