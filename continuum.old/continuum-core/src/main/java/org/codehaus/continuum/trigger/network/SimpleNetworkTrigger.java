package org.codehaus.continuum.trigger.network;

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
 * @version $Id: SimpleNetworkTrigger.java,v 1.7 2004-07-11 23:56:25 trygvis Exp $
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
