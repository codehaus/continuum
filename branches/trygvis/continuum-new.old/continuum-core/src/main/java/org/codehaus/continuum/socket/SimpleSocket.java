package org.codehaus.continuum.socket;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: SimpleSocket.java,v 1.1.1.1 2005-02-17 22:23:52 trygvis Exp $
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
