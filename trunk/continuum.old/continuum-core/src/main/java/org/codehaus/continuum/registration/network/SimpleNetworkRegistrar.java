package org.codehaus.plexus.continuum.registration.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.codehaus.plexus.continuum.network.ConnectionConsumer;
import org.codehaus.plexus.continuum.network.NetworkUtils;
import org.codehaus.plexus.continuum.registration.AbstractContinuumRegistrar;

/**
 * This trigger listens on a specified port and takes one line
 * of input which contains the the groupId and artifactId of the
 * project to build or the special word "all" to indicate building
 * all the projects.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: SimpleNetworkRegistrar.java,v 1.4 2004-04-07 15:56:56 trygvis Exp $
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

        try
        {
            BufferedReader reader = new BufferedReader( new InputStreamReader( input ) );

            instruction = reader.readLine().trim();
        }
        catch( IOException ex )
        {
            getLogger().fatalError( "Exception while reading instruction.", ex );
            return;
        }

        NetworkUtils.closeInput( input );
        NetworkUtils.closeOutput( output );

        getContinuum().addProject( instruction );
    }
}
