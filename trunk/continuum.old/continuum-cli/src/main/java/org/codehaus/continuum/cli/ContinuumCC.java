package org.codehaus.continuum.cli;

/*
 * LICENSE
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.UnrecognizedOptionException;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.embed.Embedder;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumCC.java,v 1.1 2004-07-07 05:05:36 trygvis Exp $
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

        cli.work( args );

        cli.stop();
    }

    public void start()
        throws Exception
    {
        embedder = new Embedder();

        embedder.start();

        plexus = embedder.getContainer();
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
