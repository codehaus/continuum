package org.codehaus.continuum.xmlrpc;

/*
 * LICENSE
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;

import org.codehaus.continuum.Continuum;
import org.codehaus.plexus.util.IOUtil;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultContinuumXmlRpcInterface.java,v 1.1 2004-06-27 19:28:46 trygvis Exp $
 */
public class DefaultContinuumXmlRpcInterface
    implements ContinuumXmlRpcInterface
{
    ///////////////////////////////////////////////////////////////////////////
    // Requirements

    private MavenProjectBuilder projectBuilder;

    private Continuum continuum;

    ///////////////////////////////////////////////////////////////////////////
    // ContinuumXmlRpcInterface Implementation

    public String registerProject( String pom )
    {
        try
        {
            File pomFile = stringToFile( pom );

            MavenProject project = projectBuilder.build( pomFile );

            continuum.addProject( project );

            return "OK";
        }
        catch( Exception ex )
        {
            return "FAILURE";
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Private

    private File stringToFile( String string )
        throws IOException
    {
        File file = File.createTempFile( "continuum", "xmlrpc" );

        FileWriter writer = new FileWriter( file );

        IOUtil.copy( string, writer );

        writer.close();

        return file;
    }
}
