package org.codehaus.continuum.network;

/*
 * LICENSE
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ConnectionConsumer.java,v 1.4 2004-07-01 15:30:57 trygvis Exp $
 */
public interface ConnectionConsumer
{
    String ROLE = ConnectionConsumer.class.getName();

    void consumeConnection( InputStream input, OutputStream output )
        throws IOException;
}
