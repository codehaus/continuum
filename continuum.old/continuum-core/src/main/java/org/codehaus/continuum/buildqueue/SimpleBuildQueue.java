package org.codehaus.continuum.buildqueue;

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

import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: SimpleBuildQueue.java,v 1.5 2004-10-09 13:01:52 trygvis Exp $
 */
public class SimpleBuildQueue extends AbstractBuildQueue
{
    /**
     * The queue of elements.
     */
    List queue = new LinkedList();

    public String dequeue()
    {
        synchronized( queue )
        {
            if ( queue.size() == 0 )
                return null;

            return (String)queue.remove( 0 );
        }
    }

    public void enqueue( String buildId )
    {
        synchronized( queue )
        {
            queue.add( buildId );
        }
    }

    /**
     * Returns the length of the queue.
     *
     * @return Returns the length of the queue.
     */
    public int getLength()
    {
        synchronized( queue )
        {
            return queue.size();
        }
    }
}
