package org.codehaus.continuum.cli;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.apache.maven.scm.Scm;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.embed.Embedder;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumCC.java,v 1.3 2004-07-27 05:42:11 trygvis Exp $
 */
public class ContinuumCC
{
    private PlexusContainer plexus;

    private Map commands;

    private Embedder embedder;

    public static void main( String[] args )
        throws Exception
    {
        ContinuumCC cli = new ContinuumCC();

        cli.start();

        int exitCode = cli.work( args );

        try
        {
            cli.stop();
        }
        catch( Exception ex )
        {
            System.err.println( "Exception while shutting down the container, ignored." );

            ex.printStackTrace( System.err );
        }

        System.exit( exitCode );
    }

    public void start()
        throws Exception
    {
        embedder = new Embedder();

        embedder.start();

        plexus = embedder.getContainer();

        plexus.lookup( Scm.ROLE, "cvs" );
    }

    public void stop()
        throws Exception
    {
        embedder.stop();
    }

    public int work( String[] args )
        throws Exception
    {
        if ( embedder == null )
        {
            throw new ContinuumException( "Must be started first." );
        }

        commands = plexus.lookupMap( CliCommand.ROLE );

        if ( args.length == 0 )
        {
            throw new Exception( "The first argument on the command line must be the command name." );
        }

        String commandName = args[0];

        CliCommand command = getCommand( commandName );

        try
        {
            // Remove the first argument which is the command name.
            List argsList = new ArrayList( Arrays.asList( args ) );

            argsList.remove( 0 );

            args = (String[]) argsList.toArray( new String[ args.length - 1] );

            command.execute( args );
        }
        catch( MissingOptionException ex )
        {
            System.err.println( ex.getMessage() );

            System.err.println( command.getCommandLineHelp() );

            return -1;
        }
        catch( UnrecognizedOptionException ex )
        {
            System.err.println( ex.getMessage() );

            System.err.println( command.getCommandLineHelp() );

            return -1;
        }
        catch( ContinuumException ex )
        {
            ex.printStackTrace( System.out );

            return -1;
        }

        return 0;
    }

    private CliCommand getCommand( String name )
        throws Exception
    {
        CliCommand command = (CliCommand) commands.get( name );

        if ( command == null )
        {
            throw new Exception( "No such command: '" + name + "'." );
        }

        return command;
    }

    public PlexusContainer getPlexus()
    {
        return plexus;
    }
}
