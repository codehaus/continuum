package org.codehaus.continuum.trigger.network;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.network.ConnectionConsumer;
import org.codehaus.continuum.trigger.AbstractContinuumTrigger;
import org.codehaus.plexus.util.IOUtil;

/**
 * This trigger listens on a specified port and takes one line
 * of input which contains the the groupId and artifactId of the
 * project to build or the special word "all" to indicate building
 * all the projects.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: SimpleNetworkTrigger.java,v 1.8 2004-07-27 05:42:14 trygvis Exp $
 */
public class SimpleNetworkTrigger
    extends AbstractContinuumTrigger
    implements ConnectionConsumer
{
    // ----------------------------------------------------------------------
    // ConnectionConsumer Implementation
    // ----------------------------------------------------------------------

    public void consumeConnection( InputStream input, OutputStream output )
        throws IOException
    {
        PrintWriter printer = new PrintWriter( output );

        BufferedReader reader = new BufferedReader( new InputStreamReader( input ) );

        String id;

        try
        {
            id = reader.readLine();

            if( id.length() == 0 )
            {
                printer.println( "ERROR" );

                printer.println( "Error in input, expected format: id." );

                return;
            }

            String buildId = getContinuum().buildProject( id );

            printer.println( "OK" );

            printer.println( "id=" + buildId );

            printer.println( "Build of " + id + " scheduled." );
        }
        catch( ContinuumException ex )
        {
            printer.println( "ERROR" );

            ex.printStackTrace( printer );
        }
        finally
        {
            IOUtil.close( printer );
        }
    }
}
