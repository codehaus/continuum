package org.codehaus.continuum.notification.console;

/*
 * LICENSE
 */

import org.apache.maven.project.MavenProject;

import org.codehaus.continuum.notification.ContinuumNotifier;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ConsoleNotifier.java,v 1.4 2004-05-13 17:48:17 trygvis Exp $
 */
public class ConsoleNotifier
    implements ContinuumNotifier
{
    public void buildStarted(MavenProject project)
    {
        out( project, "Build started." );
    }

    public void checkoutStarted(MavenProject project)
    {
        out( project, "Checkout started." );
    }

    public void checkoutComplete(MavenProject project, Exception ex)
    {
        out( project, "Checkout complete.", ex );
    }

    public void runningGoals(MavenProject project)
    {
        out( project, "Running goals." );
    }

    public void goalsCompleted(MavenProject project, Exception ex)
    {
        out( project, "Goals completed.", ex );
    }

    public void buildComplete(MavenProject project, Exception ex)
    {
        out( project, "Build complete.", ex );
    }

    private void out( MavenProject project, String msg )
    {
        out( project, msg, null );
    }

    private void out( MavenProject project, String msg, Exception ex )
    {
        System.out.println( project.getId() + ":" + msg );

        if ( ex != null )
        {
            // TODO: better reporting
            String message = ex.getMessage();

            System.out.println( "Message: " + message );
        }
    }
}
