package org.codehaus.continuum.utils;

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
 * A simple non-reentrant mutual exclusion lock.
 * The lock is free upon construction. Each acquire gets the
 * lock, and each release frees it. Releasing a lock that
 * is already free has no effect.
 * <p/>
 * This implementation makes no attempt to provide any fairness
 * or ordering guarantees. If you need them, consider using one of
 * the Semaphore implementations as a locking mechanism.
 * <p/>
 * <b>Sample usage</b><br>
 * <p/>
 * Mutex can be useful in constructions that cannot be
 * expressed using java synchronized blocks because the
 * acquire/release pairs do not occur in the same method or
 * code block. For example, you can use them for hand-over-hand
 * locking across the nodes of a linked list. This allows
 * extremely fine-grained locking,  and so increases
 * potential concurrency, at the cost of additional complexity and
 * overhead that would normally make this worthwhile only in cases of
 * extreme contention.
 * <pre>
 * class Node {
 *   Object item;
 *   Node next;
 *   Mutex lock = new Mutex(); // each node keeps its own lock
 * <p/>
 *   Node(Object x, Node n) { item = x; next = n; }
 * }
 * <p/>
 * class List {
 *    protected Node head; // pointer to first node of list
 * <p/>
 *    // Use plain java synchronization to protect head field.
 *    //  (We could instead use a Mutex here too but there is no
 *    //  reason to do so.)
 *    protected synchronized Node getHead() { return head; }
 * <p/>
 *    boolean search(Object x) throws InterruptedException {
 *      Node p = getHead();
 *      if (p == null) return false;
 * <p/>
 *      //  (This could be made more compact, but for clarity of illustration,
 *      //  all of the cases that can arise are handled separately.)
 * <p/>
 *      p.lock.acquire();              // Prime loop by acquiring first lock.
 *                                     //    (If the acquire fails due to
 *                                     //    interrupt, the method will throw
 *                                     //    InterruptedException now,
 *                                     //    so there is no need for any
 *                                     //    further cleanup.)
 *      for (;;) {
 *        if (x.equals(p.item)) {
 *          p.lock.release();          // release current before return
 *          return true;
 *        }
 *        else {
 *          Node nextp = p.next;
 *          if (nextp == null) {
 *            p.lock.release();       // release final lock that was held
 *            return false;
 *          }
 *          else {
 *            try {
 *              nextp.lock.acquire(); // get next lock before releasing current
 *            }
 *            catch (InterruptedException ex) {
 *              p.lock.release();    // also release current if acquire fails
 *              throw ex;
 *            }
 *            p.lock.release();      // release old lock now that new one held
 *            p = nextp;
 *          }
 *        }
 *      }
 *    }
 * <p/>
 *    synchronized void add(Object x) { // simple prepend
 *      // The use of `synchronized'  here protects only head field.
 *      // The method does not need to wait out other traversers
 *      // who have already made it past head.
 * <p/>
 *      head = new Node(x, head);
 *    }
 * <p/>
 *    // ...  other similar traversal and update methods ...
 * }
 * </pre>
 * <p/>
 * <p/>
 * See: [<a href="http://gee.cs.oswego.edu/dl/classes/EDU/oswego/cs/dl/util/concurrent/intro.html">Introduction to this package.</a>]
 *
 * @version $Id: Mutex.java,v 1.1.1.1 2005-02-17 22:23:54 trygvis Exp $
 */
public class Mutex
{
    /**
     * The lock status *
     */
    protected boolean inuse_ = false;

    public void acquire() throws InterruptedException
    {
        if ( Thread.interrupted() )
            throw new InterruptedException();
        synchronized ( this )
        {
            try
            {
                while ( inuse_ )
                    wait();
                inuse_ = true;
            }
            catch ( InterruptedException ex )
            {
                notify();
                throw ex;
            }
        }
    }

    public synchronized void release()
    {
        inuse_ = false;
        notify();
    }

    public boolean attempt( long msecs ) throws InterruptedException
    {
        if ( Thread.interrupted() )
            throw new InterruptedException();
        synchronized ( this )
        {
            if ( !inuse_ )
            {
                inuse_ = true;
                return true;
            }
            else if ( msecs <= 0 )
                return false;
            else
            {
                long waitTime = msecs;
                long start = System.currentTimeMillis();
                try
                {
                    for ( ; ; )
                    {
                        wait( waitTime );
                        if ( !inuse_ )
                        {
                            inuse_ = true;
                            return true;
                        }
                        waitTime = msecs - ( System.currentTimeMillis() - start );
                        if ( waitTime <= 0 )
                            return false;
                    }
                }
                catch ( InterruptedException ex )
                {
                    notify();
                    throw ex;
                }
            }
        }
    }
}
