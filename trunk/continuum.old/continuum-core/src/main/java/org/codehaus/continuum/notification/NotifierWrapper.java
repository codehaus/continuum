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
import org.codehaus.plexus.logging.Logger;

/**
 * A simple wrapper around a @see Notifier.
 *
 * It's for anything that needs a <code>Notifier</code> but don't care about the
 * exceptions. The exceptions are just reported with
 * <code>logger.fatalError( ex )</code>.
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: NotifierWrapper.java,v 1.8 2004-10-09 13:01:53 trygvis Exp $
 */
public class NotifierWrapper
{
    private ContinuumNotifier notifier;

    private Logger logger;

    public NotifierWrapper( ContinuumNotifier notifier, Logger logger )
    {
        this.notifier = notifier;
        this.logger = logger;
    }

    public void buildStarted( ContinuumBuild build )
    {
        try
        {
            notifier.buildStarted( build );
        }
        catch( ContinuumException e )
        {
            logger.fatalError( "Error while notifying.", e );
        }
    }

    public void checkoutStarted( ContinuumBuild build )
    {
        try
        {
            notifier.checkoutStarted( build );
        }
        catch( ContinuumException e )
        {
            logger.fatalError( "Error while notifying.", e );
        }
    }

    public void checkoutComplete( ContinuumBuild build )
    {
        try
        {
            notifier.checkoutComplete( build );
        }
        catch( ContinuumException e )
        {
            logger.fatalError( "Error while notifying.", e );
        }
    }

    public void runningGoals( ContinuumBuild build )
    {
        try
        {
            notifier.runningGoals( build );
        }
        catch( ContinuumException e )
        {
            logger.fatalError( "Error while notifying.", e );
        }
    }

    public void goalsCompleted( ContinuumBuild build )
    {
        try
        {
            notifier.goalsCompleted( build );
        }
        catch( ContinuumException e )
        {
            logger.fatalError( "Error while notifying.", e );
        }
    }

    public void buildComplete( ContinuumBuild build )
    {
        try
        {
            notifier.buildComplete( build );
        }
        catch( ContinuumException e )
        {
            logger.fatalError( "Error while notifying.", e );
        }
    }
}
