package org.codehaus.continuum.notification;

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

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.plexus.logging.AbstractLogEnabled;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AbstractContinuumNotifier.java,v 1.1.1.1 2005-02-17 22:23:51 trygvis Exp $
 */
public abstract class AbstractContinuumNotifier
    extends AbstractLogEnabled
    implements ContinuumNotifier
{

    // ----------------------------------------------------------------------
    // ContinuumNotifier Implementation
    // ----------------------------------------------------------------------

    public void buildStarted( ContinuumBuild build )
        throws ContinuumException
    {
    }

    public void checkoutStarted( ContinuumBuild build )
        throws ContinuumException
    {
    }

    public void checkoutComplete( ContinuumBuild build )
        throws ContinuumException
    {
    }

    public void runningGoals( ContinuumBuild build )
        throws ContinuumException
    {
    }

    public void goalsCompleted( ContinuumBuild build )
        throws ContinuumException
    {
    }

    public void buildComplete( ContinuumBuild build )
        throws ContinuumException
    {
    }
}