package org.codehaus.continuum.notification;

/*
 * LICENSE
 */

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.project.BuildResult;
import org.codehaus.plexus.logging.Logger;

/**
 * A simple wrapper around a {@see Notifier}.
 * 
 * It's for anything that needs a <code>Notifier</code> but don't care about the
 * exceptions. The exceptions are just reported with
 * <code>logger.fatalError( ex )</code>.
 * 
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: NotifierWrapper.java,v 1.5 2004-07-07 02:34:34 trygvis Exp $
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

    public void buildStarted( BuildResult build )
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

    public void checkoutStarted( BuildResult build )
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

    public void checkoutComplete( BuildResult build, Exception ex )
    {
        try
        {
            notifier.checkoutComplete( build, ex );
        }
        catch( ContinuumException e )
        {
            logger.fatalError( "Error while notifying.", e );
        }
    }

    public void runningGoals( BuildResult build )
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

    public void goalsCompleted( BuildResult build, Exception ex )
    {
        try
        {
            notifier.goalsCompleted( build, ex );
        }
        catch( ContinuumException e )
        {
            logger.fatalError( "Error while notifying.", e );
        }
    }

    public void buildComplete( BuildResult build, Exception ex )
    {
        try
        {
            notifier.buildComplete( build, ex );
        }
        catch( ContinuumException e )
        {
            logger.fatalError( "Error while notifying.", e );
        }
    }
}
