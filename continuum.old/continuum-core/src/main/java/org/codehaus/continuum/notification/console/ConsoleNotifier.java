package org.codehaus.continuum.notification.console;

/*
 * LICENSE
 */

import org.codehaus.continuum.notification.ContinuumNotifier;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.plexus.logging.AbstractLogEnabled;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ConsoleNotifier.java,v 1.7 2004-07-27 00:06:04 trygvis Exp $
 */
public class ConsoleNotifier
    extends AbstractLogEnabled
    implements ContinuumNotifier
{
    public void buildStarted( ContinuumBuild build )
    {
        out( build, "Build started." );
    }

    public void checkoutStarted( ContinuumBuild build )
    {
        out( build, "Checkout started." );
    }

    public void checkoutComplete( ContinuumBuild build )
    {
        out( build, "Checkout complete." );
    }

    public void runningGoals( ContinuumBuild build )
    {
        out( build, "Running goals." );
    }

    public void goalsCompleted( ContinuumBuild build )
    {
        if ( build.getBuildResult() != null )
        {
            out( build, "Goals completed. success: " + build.getBuildResult().isSuccess() );
        }
        else
        {
            out( build, "Goals completed." );
        }
    }

    public void buildComplete( ContinuumBuild build )
    {
        if ( build.getBuildResult() != null )
        {
            out( build, "Build complete. success: " + build.getState() );
        }
        else
        {
            out( build, "Build complete." );
        }
    }

    private void out( ContinuumBuild build, String msg )
    {
        System.out.println( build.getId() + ":" + msg );

        Throwable ex = build.getError();

        if ( ex != null )
        {
            // TODO: better reporting
//            String message = ex.getMessage();

//            System.out.println( "Message: " + message );
            ex.printStackTrace( System.out );
        }
    }
}
