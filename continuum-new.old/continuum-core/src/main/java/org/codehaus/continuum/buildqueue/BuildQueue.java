package org.codehaus.continuum.buildqueue;

/*
 * The MIT License
 *
 * Copyright (c) 2004, Jason van Zyl and Trygve Laugst�l
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

/**
 * A queue of build job ids.
 * <p/>
 * A <code>BuildQueue</code> implementation MUST be thread safe.
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: BuildQueue.java,v 1.1.1.1 2005-02-17 22:23:50 trygvis Exp $
 */
public interface BuildQueue
{
    static String ROLE = BuildQueue.class.getName();

    /**
     * Returns a element from the queue.
     * <p/>
     * Returns <code>null</code> if the queue is empty.
     *
     * @return Returns a element from the queue or <code>null</code> if the queue is empty.
     */
    String dequeue();

    /**
     * @param projectId The id of the project to enqueue.
     */
    void enqueue( String projectId );

    /**
     * Returns the length of the queue.
     *
     * @return Returns the length of the queue.
     */
    int getLength();
}
