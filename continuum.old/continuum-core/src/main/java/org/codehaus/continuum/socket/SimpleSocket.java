package org.codehaus.continuum.socket;

/*
 * LICENSE
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.codehaus.plexus.util.IOUtil;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: SimpleSocket.java,v 1.1 2004-10-07 11:52:03 trygvis Exp $
 */
public class SimpleSocket
{
    private Socket socket;

    private InputStream input;

    private BufferedReader reader;

    private OutputStream output;

    private PrintWriter writer;

    public SimpleSocket( String host, int port )
        throws UnknownHostException, IOException
    {
        socket = new Socket( host, port );

        setup( socket.getInputStream(), socket.getOutputStream() );
    }

    public SimpleSocket( InputStream input, OutputStream output )
        throws IOException
    {
        if ( input == null )
        {
            throw new IllegalArgumentException( "input cannot be null" );
        }

        if ( output == null )
        {
            throw new IllegalArgumentException( "output cannot be null" );
        }

        setup( input, output );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public String readLine()
        throws IOException
    {
        return reader.readLine();
    }

    public void writeLine( String line )
        throws IOException
    {
        writer.println( line );

        writer.flush();

        output.flush();
    }

    public void close()
    {
        IOUtil.close( reader );
        IOUtil.close( writer );
        IOUtil.close( input );
        IOUtil.close( output );

        if ( socket != null )
        {
            try
            {
                socket.close();
            }
            catch ( IOException e )
            {
                // ignore
            }
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private void setup( InputStream input, OutputStream output )
        throws IOException
    {
        this.input = input;

        this.output = output;

        reader = new BufferedReader( new InputStreamReader( input ) );

        writer = new PrintWriter( new OutputStreamWriter( output ) );
    }
}
