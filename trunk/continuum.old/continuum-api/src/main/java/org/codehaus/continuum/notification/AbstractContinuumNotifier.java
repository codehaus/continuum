package org.codehaus.continuum.notification;

/*
 * LICENSE
 */

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.plexus.logging.AbstractLogEnabled;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AbstractContinuumNotifier.java,v 1.1 2004-07-27 00:06:02 trygvis Exp $
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
