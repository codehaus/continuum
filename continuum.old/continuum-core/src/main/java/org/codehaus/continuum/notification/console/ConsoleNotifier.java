package org.codehaus.continuum.notification.console;

/*
 * Copyright (c) 2004, Jason van Zyl and Trygve Laugstøl
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import org.codehaus.continuum.notification.ContinuumNotifier;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.plexus.logging.AbstractLogEnabled;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ConsoleNotifier.java,v 1.8 2004-07-27 05:42:13 trygvis Exp $
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
