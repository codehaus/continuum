package org.codehaus.plexus.continuum.network;

/*
 * LICENSE
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ConnectionConsumer.java,v 1.2 2004-04-22 20:03:41 trygvis Exp $
 */
public interface ConnectionConsumer {
    void consumeConnection( InputStream input, OutputStream output )
        throws IOException;
}
