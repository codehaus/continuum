package org.codehaus.plexus.continuum.network;

/*
 * LICENSE
 */

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: SimpleServerSocketConnectionFactoryTest.java,v 1.2 2004-04-24 23:54:14 trygvis Exp $
 */
public class SimpleServerSocketConnectionFactoryTest
    extends PlexusTestCase
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

        factory = (ConnectionFactory)lookup( ConnectionFactory.class.getName() );

        System.err.println( "Connecting..." );
        socket = new Socket( "127.0.0.1", port );
        System.err.println( "Connected" );

        output = socket.getOutputStream();
        input = socket.getInputStream();

        for( i = 0; i < rawData.length; i++)
            output.write( rawData[i] );

        readData = new byte[ rawData.length ];

        for( i = 0; i < readData.length; i++ )
        {
            data = input.read();

            if( data == -1 )
                fail( "Unexpected end of stream." );

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
            assertEquals( "Checking byte #" + i, expected[ i ], actual[ i ] );
    }
}
