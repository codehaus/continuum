package org.codehaus.plexus.continuum;

/*
 * LICENSE
 */

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.MavenCore;
import org.apache.maven.project.MavenProject;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.util.FileUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultContinuumBuilder.java,v 1.1 2004-04-24 23:54:12 trygvis Exp $
 */
public class DefaultContinuumBuilder
    extends AbstractLogEnabled
    implements Initializable, ContinuumBuilder
{
    // configuration

    private String checkoutDirectory;

    // requirements

    private MavenCore maven;

    ///////////////////////////////////////////////////////////////////////////
    // Lifecycle

    public void initialize()
        throws Exception
    {
    
    }

    ///////////////////////////////////////////////////////////////////////////
    // ContinuumBuilder implementation

    /**
     * This method does the actual building of a project.
     * 
     * @param projectBuild
     * @throws ContinuumException
     */
    public void build( MavenProject descriptor )
    {
        List messages;

        try
        {
            getLogger().info( "Building " + descriptor.getName() );

            String coDir = checkoutDirectory + File.separator + 
                descriptor.getGroupId() + File.separator + 
                descriptor.getArtifactId();

            // TODO: Use maven here
            // maven.execute( descriptor, "scm:checkout" );

            // We need to check out the sources

            projectBuild.getProjectScm().checkout( coDir );

            File file = new File( coDir, "project.xml" );

            MavenProject project = projectBuilder.build( file );

            messages = compileProject( project );

            notify( messages, projectBuild );

            FileUtils.forceDelete( coDir );
        }
        catch ( Exception e )
        {
            StringWriter writer = new StringWriter();

            PrintWriter w = new PrintWriter( writer );

            e.printStackTrace( w );

            messages = new ArrayList();

            messages.add( "Exception while building project." );
            messages.add( writer.toString() );

//            notify( messages, descriptor );
        }
    }
}
