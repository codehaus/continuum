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
 * @version $Id: NotifierWrapper.java,v 1.2 2004-04-26 00:21:18 trygvis Exp $
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

    public void buildStarted( MavenProject project )
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

    public void checkoutStarted( MavenProject project )
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

    public void checkoutComplete( MavenProject project, Exception ex )
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

    public void runningGoals( MavenProject project )
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

    public void goalsCompleted( MavenProject project, Exception ex )
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

    public void buildComplete( MavenProject project, Exception ex )
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
