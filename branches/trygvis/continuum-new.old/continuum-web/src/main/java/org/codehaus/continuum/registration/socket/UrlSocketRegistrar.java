package org.codehaus.continuum.registration.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.codehaus.continuum.network.ConnectionConsumer;
import org.codehaus.continuum.registration.AbstractContinuumRegistrar;
import org.codehaus.continuum.socket.SimpleSocket;
import org.codehaus.continuum.utils.ContinuumUtils;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: UrlSocketRegistrar.java,v 1.1.1.1 2005-02-17 22:23:55 trygvis Exp $
 */
public class UrlSocketRegistrar
    extends AbstractContinuumRegistrar
    implements ConnectionConsumer
{
    // ----------------------------------------------------------------------
    // ConnectionConsumer Implementation
    // ----------------------------------------------------------------------

    public void consumeConnection( InputStream input, OutputStream output )
        throws IOException
    {
        SimpleSocket socket = new SimpleSocket( input, output );

        try
        {
            String url = socket.readLine();

            URL u = new URL( url );

            String projectId = getContinuum().addProject( u, "maven2" );

            socket.writeLine( "OK" );

            socket.writeLine( "id=" + projectId );
        }
        catch( Exception ex )
        {
            socket.writeLine( "ERROR" );

            String stackTrace = ContinuumUtils.getExceptionStackTrace( ex );

            socket.writeLine( "Exception while adding the project." );

            socket.writeLine( stackTrace );
        }
    }
}
