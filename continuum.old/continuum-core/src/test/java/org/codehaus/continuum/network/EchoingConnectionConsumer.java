package org.codehaus.plexus.continuum.network;

/*
 * LISENCE
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.codehaus.plexus.logging.AbstractLogEnabled;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: EchoingConnectionConsumer.java,v 1.1 2004-04-07 15:56:56 trygvis Exp $
 */
public class EchoingConnectionConsumer
    extends AbstractLogEnabled
    implements ConnectionConsumer
{
    public void consumeConnection(InputStream input, OutputStream output)
        throws IOException
    {
        int data;

        while ( (data = input.read() ) != -1 )
        {
            getLogger().info( "Writing " + Integer.toHexString( data ) );
            output.write( data );
        }

        output.close();
        input.close();
    }
}
