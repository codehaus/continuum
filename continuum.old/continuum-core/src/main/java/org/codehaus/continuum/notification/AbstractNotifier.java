package org.codehaus.continuum.notification;

/*
 * LICENSE
 */

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.project.BuildResult;
import org.codehaus.plexus.logging.AbstractLogEnabled;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AbstractNotifier.java,v 1.3 2004-07-19 16:28:17 trygvis Exp $
 */
public abstract class AbstractNotifier
    extends AbstractLogEnabled
    implements ContinuumNotifier
{

    // ----------------------------------------------------------------------
    // ContinuumNotifier Implementation
    // ----------------------------------------------------------------------

    public void buildStarted( BuildResult build )
        throws ContinuumException
    {
    }

    public void checkoutStarted( BuildResult build )
        throws ContinuumException
    {
    }

    public void checkoutComplete( BuildResult build, Exception ex )
        throws ContinuumException
    {
    }

    public void runningGoals( BuildResult build )
        throws ContinuumException
    {
    }

    public void goalsCompleted( BuildResult build, Exception ex )
        throws ContinuumException
    {
    }

    public void buildComplete( BuildResult build, Exception ex )
        throws ContinuumException
    {
    }

/*
    protected String createMessage( List messages, MavenProject project )
    {
        StringBuffer message = new StringBuffer();

        // Notification is there are failures.
        if ( messages.size() > 0 )
        {
            for ( Iterator j = messages.iterator(); j.hasNext(); )
            {
                message.append( j.next() ).append( "\n" );
            }
        }
        else
        {
            message.append( "Build OK." );
        }

        return message.toString();
    }
*/
}
