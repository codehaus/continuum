package org.codehaus.continuum.builder.maven2;

/*
 * LICENSE
 */

import java.io.IOException;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;
import org.codehaus.plexus.util.cli.StreamPumper;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l </a>
 * @version $Id: CommandLineUtils.java,v 1.2 2004-10-06 13:39:15 trygvis Exp $
 */
public abstract class CommandLineUtils
{
    public static class StringStreamConsumer
        implements StreamConsumer
    {
        private StringBuffer string = new StringBuffer();

        private String ls = System.getProperty( "line.separator" );

        public void consumeLine( String line )
        {
            string.append( line + ls );
        }

        public String getOutput()
        {
            return string.toString();
        }
    }

    public static int executeCommandLine( Commandline cl, StreamConsumer systemOut, StreamConsumer systemErr )
        throws ContinuumException
    {
        if ( cl == null )
        {
            throw new ContinuumException( "The command line cannot be null." );
        }

        Process p;

//        System.out.println( "Executing: " + cl );
//        System.out.println( "pwd: " + cl.getWorkingDirectory().getAbsolutePath() );

        try
        {
            p = cl.execute();
        }
        catch( IOException ex )
        {
            throw new ContinuumException( "Error while executing external command.", ex );
        }

        StreamPumper inputPumper = new StreamPumper( p.getInputStream(), systemOut );

        StreamPumper errorPumper = new StreamPumper( p.getErrorStream(), systemErr );

        inputPumper.start();

        errorPumper.start();

        try
        {
            int returnValue = p.waitFor();

            while( !inputPumper.isDone() )
            {
                Thread.sleep( 0 );
            }

            while( !errorPumper.isDone() )
            {
                Thread.sleep( 0 );
            }

            return returnValue;
        }
        catch( InterruptedException ex )
        {
            throw new ContinuumException( "Error while executing external command.", ex );
        }
        finally
        {
            inputPumper.close();

            errorPumper.close();
        }
    }
}
