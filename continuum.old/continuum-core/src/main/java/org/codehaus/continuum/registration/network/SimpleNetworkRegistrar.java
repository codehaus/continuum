package org.codehaus.continuum.registration.network;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;

import org.codehaus.continuum.network.ConnectionConsumer;
import org.codehaus.continuum.registration.AbstractContinuumRegistrar;
import org.codehaus.plexus.util.IOUtil;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: SimpleNetworkRegistrar.java,v 1.8 2004-07-01 15:30:58 trygvis Exp $
 */
public class SimpleNetworkRegistrar
    extends AbstractContinuumRegistrar
    implements ConnectionConsumer
{
    /** @requirement */
    private MavenProjectBuilder projectBuilder;

    /** @default ${maven.home}/repository */
    private String localRepository;

    /////////////////////////////////////////////////////////////////////////
    // ConnectionConsumer Implementation

    public void consumeConnection( InputStream input, OutputStream output )
    {
        PrintWriter printer = new PrintWriter( output );

        try
        {
            BufferedReader reader = new BufferedReader( new InputStreamReader( input ) );

            String pom = IOUtil.toString( reader );

            File file = File.createTempFile( "continuum", "project" );

            file.deleteOnExit();

            IOUtil.copy( pom, new FileWriter( file ) );

            MavenProject project = projectBuilder.build( file, localRepository );

            getContinuum().addProject( project );

            file.delete();

            printer.println( "OK" );
        }
        catch( Exception ex )
        {
            error( printer, "Exception while creating the project.", ex );
        }
        finally
        {
            printer.flush();
        }
    }

    private void error( PrintWriter printer, String message, Throwable ex )
    {
        printer.println( "ERROR" );
        printer.println( "Error adding project: " + ex.getMessage() );
        ex.printStackTrace( printer );
    }
}
