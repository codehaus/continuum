package org.codehaus.continuum.notification.console;

/*
 * LICENSE
 */

import org.codehaus.continuum.notification.ContinuumNotifier;
import org.codehaus.continuum.project.BuildResult;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ConsoleNotifier.java,v 1.6 2004-07-03 03:21:15 trygvis Exp $
 */
public class ConsoleNotifier
    implements ContinuumNotifier
{
    public void buildStarted( BuildResult build )
    {
        out( build, "Build started." );
    }

    public void checkoutStarted( BuildResult build )
    {
        out( build, "Checkout started." );
    }

    public void checkoutComplete( BuildResult build, Exception ex )
    {
        out( build, "Checkout complete.", ex );
    }

    public void runningGoals( BuildResult build )
    {
        out( build, "Running goals." );
    }

    public void goalsCompleted( BuildResult build, Exception ex )
    {
        out( build, "Goals completed.", ex );
    }

    public void buildComplete( BuildResult build, Exception ex )
    {
        out( build, "Build complete.", ex );
    }

    private void out( BuildResult build, String msg )
    {
        out( build, msg, null );
    }

    private void out( BuildResult build, String msg, Exception ex )
    {
        System.out.println( build.getBuildId() + ":" + msg );

        if ( ex != null )
        {
            // TODO: better reporting
//            String message = ex.getMessage();

//            System.out.println( "Message: " + message );
            ex.printStackTrace( System.out );
        }
    }
}
