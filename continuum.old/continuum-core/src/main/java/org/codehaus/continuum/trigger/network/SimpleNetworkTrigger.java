package org.codehaus.plexus.continuum.trigger.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.codehaus.plexus.continuum.ContinuumException;
import org.codehaus.plexus.continuum.network.ConnectionConsumer;
import org.codehaus.plexus.continuum.trigger.AbstractContinuumTrigger;
import org.codehaus.plexus.util.IOUtil;

/**
 * This trigger listens on a specified port and takes one line
 * of input which contains the the groupId and artifactId of the
 * project to build or the special word "all" to indicate building
 * all the projects.
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: SimpleNetworkTrigger.java,v 1.3 2004-04-24 23:54:13 trygvis Exp $
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
        String project, groupId, artifactId;
        int i;

        try
        {
            project = reader.readLine();

            i = project.indexOf( ':' );

            if( i == -1 )
            {
                printer.println( "ERROR" );
                printer.println( "Error in input, expected format: groupId:artifactId." );

                return;
            }

            groupId = project.substring( 0, i ).trim();
            artifactId = project.substring( i + 1 ).trim();

            if( groupId.length() == 0 )
            {
                printer.println( "ERROR" );
                printer.println( "Error in input, expected format: groupId:artifactId." );

                return;
            }

            if( artifactId.length() == 0 )
            {
                printer.println( "ERROR" );
                printer.println( "Error in input, expected format: groupId:artifactId." );

                return;
            }

            String jobId = getContinuum().buildProject( groupId, artifactId );

            printer.println( "OK" );
            printer.println( jobId );
            printer.println( "Build of " + groupId + ":" + artifactId + " scheduled. Job #" + jobId);
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
