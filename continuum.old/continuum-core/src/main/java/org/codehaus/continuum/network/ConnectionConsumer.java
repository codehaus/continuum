package org.codehaus.continuum.network;

/*
 * LICENSE
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ConnectionConsumer.java,v 1.3 2004-05-13 17:48:17 trygvis Exp $
 */
public interface ConnectionConsumer {
    void consumeConnection( InputStream input, OutputStream output )
        throws IOException;
}
