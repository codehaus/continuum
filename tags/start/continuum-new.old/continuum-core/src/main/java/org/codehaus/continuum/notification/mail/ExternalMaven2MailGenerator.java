package org.codehaus.continuum.notification.mail;

/*
 * The MIT License
 *
 * Copyright (c) 2004, Jason van Zyl and Trygve Laugst�l
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

import org.codehaus.continuum.builder.shell.ShellBuildResult;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumBuildResult;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ContinuumProjectState;
import org.codehaus.plexus.logging.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Date;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ExternalMaven2MailGenerator.java,v 1.1.1.1 2005-02-17 22:23:51 trygvis Exp $
 */
public class ExternalMaven2MailGenerator
    extends AbstractMailGenerator
{
    private String hostName;

    public ExternalMaven2MailGenerator( Logger logger )
    {
        super( logger );
    }

    public String generateContent( ContinuumProject project, ContinuumBuild build, ContinuumBuild lastBuild )
    {
        StringWriter message = new StringWriter();

        PrintWriter output = new PrintWriter( message );

        ShellBuildResult result = (ShellBuildResult) build.getBuildResult();

        writeStats( output, build, lastBuild );

        ContinuumProjectState state = build.getState();

        if ( state == ContinuumProjectState.OK ||
            state == ContinuumProjectState.FAILED )
        {
            line( output );
            output.println( "System out" );
            line( output );
            output.print( result.getStandardOutput() );
            line( output );
            output.println();
            output.println();
            line( output );
            output.println( "System err" );
            line( output );
            output.print( result.getStandardError() );
            line( output );
        }

        output.close();

        return message.toString();
    }

    public String generateSubject( ContinuumProject project, ContinuumBuild build, ContinuumBuild lastBuild )
    {
        ContinuumBuildResult result = build.getBuildResult();

        ContinuumProjectState state = build.getState();

        if ( state == ContinuumProjectState.ERROR )
        {
            return "[continuum] BUILD ERROR: " + project.getName();
        }
        else if ( state == ContinuumProjectState.OK || state == ContinuumProjectState.FAILED )
        {
            if ( !result.isSuccess() )
            {
                return "[continuum] BUILD FAILURE: " + project.getName();
            }
            else
            {
                return "[continuum] BUILD SUCCESSFUL: " + project.getName();
            }
        }
        else
        {
            return "[continuum] BUILD ERROR: Unknown build state";
        }
    }

    protected void writeStats( PrintWriter output, ContinuumBuild build, ContinuumBuild lastBuild )
    {
        long fullDiff = build.getEndTime() - build.getStartTime();

        ContinuumProjectState state = build.getState();

        line( output );

        output.println( "Build statistics" );

        output.println( "  State: " + decodeState( state ) );

        if ( lastBuild != null )
        {
            output.println( "  Last State: " + decodeState( lastBuild.getState() ) );
        }

        output.println( "  Started at: " + formatTime( build.getStartTime() ) );

        output.println( "  Finished at: " + formatTime( build.getEndTime() ) );

        output.println( "  Total time: " + formatTimeInterval( fullDiff ) );

        output.println( "  Building machine name: " + hostName );

        line( output );
    }

    protected String decodeState( ContinuumProjectState state )
    {
        if ( state == ContinuumProjectState.ERROR )
        {
            return "Error";
        }
        else if ( state == ContinuumProjectState.FAILED )
        {
            return "Failed";
        }
        else if ( state == ContinuumProjectState.OK )
        {
            return "Ok";
        }

        return "UNKNOWN";
    }

    protected String formatTime( long time )
    {
        return getDateFormat().format( new Date( time ) );
    }

    protected static String formatTimeInterval( long ms )
    {
        long secs = ms / 1000;
        long min = secs / 60;
        secs = secs % 60;

        if ( min > 0 )
        {
            return min + " minutes " + secs + " seconds";
        }
        else
        {
            return secs + " seconds";
        }
    }

    private ThreadLocal dateFormatter = new ThreadLocal();

    protected DateFormat getDateFormat()
    {
        DateFormat dateFormatter = (DateFormat) this.dateFormatter.get();

        if ( dateFormatter == null )
        {
            dateFormatter = DateFormat.getDateTimeInstance( DateFormat.FULL, DateFormat.FULL );

            this.dateFormatter.set( dateFormatter );
        }

        return dateFormatter;
    }

}
