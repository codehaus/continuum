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

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultNotifierManager.java,v 1.3 2004-07-27 05:42:13 trygvis Exp $
 */
public class DefaultNotifierManager
    extends AbstractLogEnabled
    implements Initializable, NotifierManager
{
    /** @requirement */
    private Map notifiers;

    /** */
    private List wrappers;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws Exception
    {
        wrappers = new LinkedList();

        if ( notifiers == null )
        {
            notifiers = Collections.EMPTY_MAP;
        }

        if ( notifiers.size() == 0 )
        {
            getLogger().warn( "No notifiers registered." );
        }
        else
        {
            getLogger().info( "Notifiers:" );

            for ( Iterator it = notifiers.entrySet().iterator(); it.hasNext(); )
            {
                Map.Entry entry = (Map.Entry)it.next();

                ContinuumNotifier notifier = (ContinuumNotifier) entry.getValue();

                getLogger().info( "  " + entry.getKey() );

                wrappers.add( new NotifierWrapper( notifier, getLogger() ) );
            }
        }
    }

    // ----------------------------------------------------------------------
    // NotifierManager Implementation
    // ----------------------------------------------------------------------

    public void buildStarted( ContinuumBuild build )
    {
        for ( Iterator it = wrappers.iterator(); it.hasNext(); )
        {
            ( (NotifierWrapper) it.next() ).buildStarted( build );
        }
    }

    public void checkoutStarted( ContinuumBuild build )
    {
        for ( Iterator it = wrappers.iterator(); it.hasNext(); )
        {
            ( (NotifierWrapper) it.next() ).checkoutStarted( build );
        }
    }

    /**
     * This method is called upon a completed checkout. If a error ocurred
     * <code>ex</code> will be non-null.
     *
     * @param project The whose projects checkout completed.
     * @param ex Possibly a exception.
     * @throws ContinuumException
     */
    public void checkoutComplete( ContinuumBuild build )
    {
        for ( Iterator it = wrappers.iterator(); it.hasNext(); )
        {
            ( (NotifierWrapper) it.next() ).checkoutComplete( build );
        }
    }

    public void runningGoals( ContinuumBuild build )
    {
        for ( Iterator it = wrappers.iterator(); it.hasNext(); )
        {
            ( (NotifierWrapper) it.next() ).runningGoals( build );
        }
    }

    public void goalsCompleted( ContinuumBuild build )
    {
        for ( Iterator it = wrappers.iterator(); it.hasNext(); )
        {
            ( (NotifierWrapper) it.next() ).goalsCompleted( build );
        }
    }

    public void buildComplete( ContinuumBuild build )
    {
        for ( Iterator it = wrappers.iterator(); it.hasNext(); )
        {
            ( (NotifierWrapper) it.next() ).buildComplete( build );
        }
    }
}
