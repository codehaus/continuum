package org.codehaus.continuum.registration.network;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.codehaus.continuum.network.ConnectionConsumer;
import org.codehaus.continuum.registration.AbstractContinuumRegistrar;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: SimpleNetworkRegistrar.java,v 1.10 2004-07-07 02:34:35 trygvis Exp $
 */
public class SimpleNetworkRegistrar
    extends AbstractContinuumRegistrar
    implements ConnectionConsumer
{
    /** @default ${maven.home}/repository */
    private String localRepository;

    /////////////////////////////////////////////////////////////////////////
    // ConnectionConsumer Implementation

    public void consumeConnection( InputStream input, OutputStream output )
    {
        PrintWriter printer = new PrintWriter( output );

        try
        {
            BufferedReader reader = new BufferedReader( new InputStreamReader( input ) );

            String name = reader.readLine();

            String scmConnection = reader.readLine();

            String type = reader.readLine();

            String id = getContinuum().addProject( name, scmConnection, type );

            printer.println( "OK" );

            printer.println( "id=" + id );
        }
        catch( Exception ex )
        {
            error( printer, "Exception while creating the project.", ex );
        }
        finally
        {
            printer.flush();
        }
    }

    private void error( PrintWriter printer, String message, Throwable ex )
    {
        printer.println( "ERROR" );
        printer.println( "Error adding project: " + ex.getMessage() );
        ex.printStackTrace( printer );
    }
}
