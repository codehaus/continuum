package org.codehaus.plexus.continuum.notification;

/*
 * LICENSE
 */

import org.apache.maven.project.MavenProject;

import org.codehaus.plexus.continuum.ContinuumException;
import org.codehaus.plexus.logging.Logger;

/**
 * A simple wrapper around a {@see Notifier}.
 * 
 * It's for anything that needs a <code>Notifier</code> but don't care about the
 * exceptions. The exceptions are just reported with
 * <code>logger.fatalError( ex )</code>.
 * 
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: NotifierWrapper.java,v 1.1 2004-04-24 23:54:13 trygvis Exp $
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

    void buildStarted( MavenProject project )
        throws ContinuumException
    {
        try
        {
            notifier.buildStarted( project );
        }
        catch( ContinuumException e )
        {
            logger.fatalError( "Error while notifying.", e );
        }
    }

    void checkoutStarted( MavenProject project )
        throws ContinuumException
    {
        try
        {
            notifier.checkoutStarted( project );
        }
        catch( ContinuumException e )
        {
            logger.fatalError( "Error while notifying.", e );
        }
    }

    void checkoutComplete( MavenProject project, Exception ex )
        throws ContinuumException
    {
        try
        {
            notifier.checkoutComplete( project, ex );
        }
        catch( ContinuumException e )
        {
            logger.fatalError( "Error while notifying.", e );
        }
    }

    void runningGoals( MavenProject project )
        throws ContinuumException
    {
        try
        {
            notifier.runningGoals( project );
        }
        catch( ContinuumException e )
        {
            logger.fatalError( "Error while notifying.", e );
        }
    }

    void goalsCompleted( MavenProject project, Exception ex )
        throws ContinuumException
    {
        try
        {
            notifier.goalsCompleted( project, ex );
        }
        catch( ContinuumException e )
        {
            logger.fatalError( "Error while notifying.", e );
        }
    }

    void buildComplete( MavenProject project, Exception ex )
        throws ContinuumException
    {
        try
        {
            notifier.buildComplete( project, ex );
        }
        catch( ContinuumException e )
        {
            logger.fatalError( "Error while notifying.", e );
        }
    }
}
