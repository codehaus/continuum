package org.codehaus.continuum.notification.mail;

/*
 * LICENSE
 */

import java.io.PrintWriter;
import java.io.StringWriter;

import org.codehaus.continuum.builder.maven2.ExternalMaven2BuildResult;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ContinuumProjectState;
import org.codehaus.plexus.logging.Logger;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ExternalMaven2MailGenerator.java,v 1.1 2004-10-06 14:09:44 trygvis Exp $
 */
public class ExternalMaven2MailGenerator
    extends Maven2MailGenerator
{
    public ExternalMaven2MailGenerator( Logger logger )
    {
        super( logger );
    }

    public String generateContent( ContinuumProject project, ContinuumBuild build, ContinuumBuild lastBuild )
    {
        StringWriter message = new StringWriter();

        PrintWriter output = new PrintWriter( message );

        ExternalMaven2BuildResult result = (ExternalMaven2BuildResult)build.getBuildResult();

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
}
