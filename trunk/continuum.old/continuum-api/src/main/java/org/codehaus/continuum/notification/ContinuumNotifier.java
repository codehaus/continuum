package org.codehaus.continuum.notification;

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

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.project.ContinuumBuild;

/**
 * Approximate sequence:
 * <ul>
 *  <li>buildStarted
 *  <li>checkoutStarted
 *  <li>checkoutCompleted
 *  <li>runningGoals
 *  <li>goalsCompleted
 *  <li>buildCompleted
 * </ul>
 * If any of the steps has a non-null exception a error occurred and
 * the sequence will abort.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: ContinuumNotifier.java,v 1.4 2004-10-09 13:00:17 trygvis Exp $
 */
public interface ContinuumNotifier
{
    static String ROLE = ContinuumNotifier.class.getName();

    void buildStarted( ContinuumBuild build )
        throws ContinuumException;

    void checkoutStarted( ContinuumBuild build )
        throws ContinuumException;

    /**
     * This method is called upon a completed checkout. If a error ocurred
     * <code>ex</code> will be non-null.
     *
     * @param build
     * @throws ContinuumException
     */
    void checkoutComplete( ContinuumBuild build )
        throws ContinuumException;

    void runningGoals( ContinuumBuild build )
        throws ContinuumException;

    void goalsCompleted( ContinuumBuild build )
        throws ContinuumException;

    void buildComplete( ContinuumBuild build )
        throws ContinuumException;
}
