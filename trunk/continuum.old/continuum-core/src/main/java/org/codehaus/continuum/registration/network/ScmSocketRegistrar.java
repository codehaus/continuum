package org.codehaus.continuum.registration.socket;

/*
 * Copyright (c) 2004, Jason van Zyl and Trygve Laugstøl
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.codehaus.continuum.network.ConnectionConsumer;
import org.codehaus.continuum.registration.AbstractContinuumRegistrar;
import org.codehaus.continuum.socket.SimpleSocket;
import org.codehaus.continuum.utils.ContinuumUtils;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: ScmSocketRegistrar.java,v 1.1 2004-10-30 16:20:55 trygvis Exp $
 */
public class ScmSocketRegistrar
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
            String scmUrl = socket.readLine();

            getStoreTransactionManager().begin();

            String id = getContinuum().addProjectFromScm( scmUrl, "maven2" );

            getStoreTransactionManager().commit();

            getStoreTransactionManager().begin();

            getContinuum().updateProject( id );

            getStoreTransactionManager().commit();

            socket.writeLine( "OK" );

            socket.writeLine( "id=" + id );
        }
        catch( Exception ex )
        {
            getStoreTransactionManager().rollback();

            socket.writeLine( "ERROR" );

            String stackTrace = ContinuumUtils.getExceptionStackTrace( ex );

            socket.writeLine( "Exception while adding the project." );

            socket.writeLine( stackTrace );
        }
    }
}
