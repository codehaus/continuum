package org.codehaus.continuum.trigger.network;

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
import java.net.InetAddress;
import java.net.ServerSocket;

/**
 * Factory implementation for vanilla TCP socket.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.3 $ $Date: 2004-07-27 05:42:14 $
 */
public class DefaultServerSocketFactory
{
    /**
     * Creates a socket on specified port.
     *
     * @param port the port (0 indicates any available port)
     * @return the created ServerSocket
     * @throws java.io.IOException if unable to create socket
     */
    public ServerSocket createServerSocket( final int port )
        throws IOException
    {
        return new ServerSocket( port );
    }

    /**
     * Creates a socket on specified port with a specified backlog.
     *
     * @param port the port (0 indicates any available port)
     * @param backlog the backlog
     * @return the created ServerSocket
     * @throws java.io.IOException if unable to create socket
     */
    public ServerSocket createServerSocket( int port, int backlog )
        throws IOException
    {
        return new ServerSocket( port, backlog );
    }

    /**
     * Creates a socket on a particular network interface on specified port
     * with a specified backlog.
     *
     * @param port the port (0 indicates any available port)
     * @param backlog the backlog
     * @param address the network interface to bind to.
     * @return the created ServerSocket
     * @throws java.io.IOException if unable to create socket
     */
    public ServerSocket createServerSocket( int port, int backlog, InetAddress address )
        throws IOException
    {
        return new ServerSocket( port, backlog, address );
    }
}

