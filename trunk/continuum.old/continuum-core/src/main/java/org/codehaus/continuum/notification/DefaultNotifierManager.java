package org.codehaus.continuum.notification;

/*
 * LICENSE
 */

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.project.BuildResult;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultNotifierManager.java,v 1.1 2004-07-07 02:34:34 trygvis Exp $
 */
public class DefaultNotifierManager
    extends AbstractLogEnabled
    implements Initializable, NotifierManager
{
    /** @requirement */
    private List notifiers;

    /** */
    private List wrappers;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws Exception
    {
        wrappers = new LinkedList();

        if ( notifiers == null || notifiers.size() == 0 )
        {
            getLogger().warn( "No notifiers registered." );
        }
        else
        {
            for ( Iterator it = notifiers.iterator(); it.hasNext(); )
            {
                ContinuumNotifier notifier = (ContinuumNotifier) it.next();

                wrappers.add( new NotifierWrapper( notifier, getLogger() ) );
            }
        }
    }

    // ----------------------------------------------------------------------
    // NotifierManager Implementation
    // ----------------------------------------------------------------------

    public void buildStarted( BuildResult build )
    {
        for ( Iterator it = wrappers.iterator(); it.hasNext(); )
        {
            ( (NotifierWrapper) it.next() ).buildStarted( build );
        }
    }
    
    public void checkoutStarted( BuildResult build )
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
    public void checkoutComplete( BuildResult build, Exception ex )
    {
        for ( Iterator it = wrappers.iterator(); it.hasNext(); )
        {
            ( (NotifierWrapper) it.next() ).checkoutComplete( build, ex );
        }
    }

    public void runningGoals( BuildResult build )
    {
        for ( Iterator it = wrappers.iterator(); it.hasNext(); )
        {
            ( (NotifierWrapper) it.next() ).runningGoals( build );
        }
    }
    
    public void goalsCompleted( BuildResult build, Exception ex )
    {
        for ( Iterator it = wrappers.iterator(); it.hasNext(); )
        {
            ( (NotifierWrapper) it.next() ).goalsCompleted( build, ex );
        }
    }
    
    public void buildComplete( BuildResult build, Exception ex )
    {
        for ( Iterator it = wrappers.iterator(); it.hasNext(); )
        {
            ( (NotifierWrapper) it.next() ).buildComplete( build, ex );
        }
    }
}
