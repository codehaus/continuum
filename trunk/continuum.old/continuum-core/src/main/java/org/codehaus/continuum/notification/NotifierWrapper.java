package org.codehaus.continuum.notification;

/*
 * LICENSE
 */

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.plexus.logging.Logger;

/**
 * A simple wrapper around a {@see Notifier}.
 * 
 * It's for anything that needs a <code>Notifier</code> but don't care about the
 * exceptions. The exceptions are just reported with
 * <code>logger.fatalError( ex )</code>.
 * 
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: NotifierWrapper.java,v 1.6 2004-07-27 00:06:04 trygvis Exp $
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
