package org.codehaus.continuum.notification;

/*
 * LICENSE
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
 * @version $Id: DefaultNotifierManager.java,v 1.2 2004-07-27 00:06:04 trygvis Exp $
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
