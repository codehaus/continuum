package org.codehaus.plexus.continuum.network;

/*
 * LISENCE
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ConnectionConsumer.java,v 1.1 2004-04-07 15:56:55 trygvis Exp $
 */
public interface ConnectionConsumer {
    void consumeConnection( InputStream input, OutputStream output )
        throws IOException;
}
