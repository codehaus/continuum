package org.codehaus.continuum.registration.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.network.ConnectionConsumer;
import org.codehaus.continuum.registration.AbstractContinuumRegistrar;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: SimpleNetworkRegistrar.java,v 1.6 2004-05-13 17:48:17 trygvis Exp $
 */
public class SimpleNetworkRegistrar
    extends AbstractContinuumRegistrar
    implements ConnectionConsumer
{
    // ----------------------------------------------------------------------
    // ConnectionConsumer Implementation
    // ----------------------------------------------------------------------

    public void consumeConnection( InputStream input, OutputStream output )
    {
        String instruction;
        PrintWriter printer = new PrintWriter( output );

        try
        {
            BufferedReader reader = new BufferedReader( new InputStreamReader( input ) );

            instruction = reader.readLine().trim();
        }
        catch( IOException ex )
        {
            getLogger().fatalError( "Exception while reading instruction.", ex );
            printer.println( "ERROR" );
            return;
        }

        try
        {
            getContinuum().addProject( instruction );

            printer.println( "OK" );
        }
        catch( ContinuumException ex )
        {
            printer.println( "ERROR" );
            printer.println( "Error adding project: " + ex.getMessage() );
            ex.printStackTrace( printer );
        }

        printer.flush();
    }
}
