package org.codehaus.continuum.notification.mail;

/*
 * LICENSE
 */

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.Date;

import org.codehaus.continuum.builder.maven2.Maven2BuildResult;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ContinuumProjectState;
import org.codehaus.plexus.logging.Logger;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: Maven2MailGenerator.java,v 1.1 2004-10-06 14:09:44 trygvis Exp $
 */
public class Maven2MailGenerator
    extends AbstractMailGenerator
{
    private final String hostName;

    public Maven2MailGenerator( Logger logger )
    {
        super( logger );

        String name;

        try
        {
            InetAddress address = InetAddress.getLocalHost();

            name = address.getCanonicalHostName();
        }
        catch( UnknownHostException ex )
        {
            name = "unknown";
        }

        hostName = name;
    }

    public String generateContent( ContinuumProject project, ContinuumBuild build, ContinuumBuild lastBuild )
    {
        StringWriter message = new StringWriter();

        PrintWriter output = new PrintWriter( message );

        Maven2BuildResult result = (Maven2BuildResult)build.getBuildResult();

        ContinuumProjectState state = build.getState();

        if ( state == ContinuumProjectState.ERROR )
        {
            output.println( "BUILD ERROR" );

            Throwable error = build.getError();

            if ( error != null )
            {
                line( output );
                output.println( "Build exeption:" );
                line( output );
                error.printStackTrace( output );
                line( output );
            }
        }
        else if ( state == ContinuumProjectState.OK || state == ContinuumProjectState.FAILED )
        {
//            ExecutionResponse response = result.getExecutionResponse();
//            String response = result.getExecutionResponse();
/*
            // TODO: add isExecutionError() in the repsponse
            if ( response.getException() != null )
            {
                subject = "[continuum] BUILD ERROR: " + project.getName();

                line( output );

                output.println( "BUILD ERROR" );

                line( output );

                Throwable error = response.getException();

                if ( error != null )
                {
                    output.println( "Build exeption:" );

                    line( output );

                    error.printStackTrace( output );

                    line( output );
                }
            }
            else */
            if ( !result.isSuccess() )
            {
                line( output );

                output.println( "BUILD FAILURE" );

                line( output );

                output.println( "Reason: " + result.getShortFailureResponse() );

                output.println( result.getLongFailureResponse() );
/*
                line( output );

                output.println( "Execution response: " );

                output.println( response );
*/
//                writeStats( output, build );
            }
            else
            {
                line( output );

                output.println( "BUILD SUCCESSFUL" );
/*
                if ( response.trim().length() > 0 )
                {
                    line( output );

                    output.println( response );
                }
*/
//                writeStats( output, build );
            }
        }
        else
        {
            getLogger().warn( "Unexpected build state: " + state );
        }

        output.close();

        return message.toString();
    }

    public String generateSubject( ContinuumProject project, ContinuumBuild build, ContinuumBuild lastBuild )
    {
        Maven2BuildResult result = (Maven2BuildResult)build.getBuildResult();

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

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

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
}
