package org.codehaus.plexus.continuum.notification.console;

/*
 * LICENSE
 */

import org.apache.maven.project.MavenProject;

import org.codehaus.plexus.continuum.ContinuumException;
import org.codehaus.plexus.continuum.notification.ContinuumNotifier;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ConsoleNotifier.java,v 1.2 2004-04-24 23:54:13 trygvis Exp $
 */
public class ConsoleNotifier
    implements ContinuumNotifier
{
/*
    public void notifyAudience(MavenProject project, String message)
        throws Exception
    {
        System.out.println( "Build of " + project.getName() + " complete!" );
        System.out.println( "Message: " + message );
    }
*/
    public void buildStarted(MavenProject project)
        throws ContinuumException
    {
    }

    public void checkoutStarted(MavenProject project)
        throws ContinuumException
    {
    }

    public void checkoutComplete(MavenProject project, Exception ex)
        throws ContinuumException
    {
    }

    public void runningGoals(MavenProject project)
        throws ContinuumException
    {
    }

    public void goalsCompleted(MavenProject project, Exception ex)
        throws ContinuumException
    {
    }

    public void buildComplete(MavenProject project, Exception ex)
        throws ContinuumException
    {
        System.out.println( "Build of " + project.getName() + " complete!" );

        if ( ex != null )
        {
            // TODO: better reporting
            String message = ex.getMessage();

            System.out.println( "Message: " + message );
        }
    }
}
