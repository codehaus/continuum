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
 * @version $Id: SimpleNetworkTrigger.java,v 1.5 2004-06-27 23:21:03 trygvis Exp $
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
        String project, id;
        int i;

        try
        {
            project = reader.readLine();

            i = project.indexOf( ':' );

            if( i == -1 )
            {
                printer.println( "ERROR" );
                printer.println( "Error in input, expected format: id." );

                return;
            }

            id = project.substring( 0, i ).trim();


            if( id.length() == 0 )
            {
                printer.println( "ERROR" );
                printer.println( "Error in input, expected format: id." );

                return;
            }

            String buildId = getContinuum().buildProject( id );

            printer.println( "OK" );
            printer.println( buildId );
            printer.println( "Build of " + id + " scheduled. Build id: " + buildId);
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
