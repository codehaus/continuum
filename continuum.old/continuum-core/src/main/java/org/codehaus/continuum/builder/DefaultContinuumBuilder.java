package org.codehaus.continuum;

/*
 * LICENSE
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.GoalNotFoundException;
import org.apache.maven.MavenCore;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.scm.Scm;

import org.codehaus.continuum.notification.ContinuumNotifier;
import org.codehaus.continuum.notification.NotifierWrapper;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultContinuumBuilder.java,v 1.4 2004-06-27 19:28:43 trygvis Exp $
 */
public class DefaultContinuumBuilder
    extends AbstractLogEnabled
    implements Initializable, ContinuumBuilder
{
    // requirements

    private MavenCore maven;

//    private Scm scm;

    private MavenProjectBuilder projectBuilder;

    private ContinuumScm scm;

    private ContinuumNotifier notifier;

    // configuration

    private String checkoutDirectory;

    // members

    private NotifierWrapper observer;

    ///////////////////////////////////////////////////////////////////////////
    // Lifecycle

    public void initialize()
        throws Exception
    {
        assertRequirement( maven, MavenCore.class );
        assertRequirement( scm, Scm.class );
        assertRequirement( projectBuilder, MavenProjectBuilder.class );
        assertRequirement( notifier, ContinuumNotifier.class );

        // TODO: create this another way
//        scm = new CvsScm();

        observer = new NotifierWrapper( notifier, getLogger() );
    }

    ///////////////////////////////////////////////////////////////////////////
    // ContinuumBuilder implementation

    /**
     * This method does the actual building of a project.
     * 
     * @param descriptor
     * @throws ContinuumException
     */
    public synchronized void build( MavenProject descriptor )
    {
        String basedir = null;

//        getLogger().info( "Building " + descriptor.getName() );
        observer.buildStarted( descriptor );

        try
        {
            observer.checkoutStarted( descriptor );

            // scm:cvs:pserver:anonymous@cvs.codehaus.org:/scm/cvspublic:plexus/plexus-components/native/continuum/src/test-projects/project1

            String[] connection = StringUtils.split( descriptor.getScm().getConnection(), ":" );

            if ( connection.length != 6 )
                throw new ContinuumException( "Invalid connection string." );

            if ( !connection[1].equals( "cvs" ) )
                throw new ContinuumException( "Continuum currently only supports 'cvs' as scm repo." );

            scm.clean( descriptor );

            basedir = scm.checkout( descriptor );

            observer.checkoutComplete( descriptor, null );
        }
        catch( Exception ex )
        {
            observer.checkoutComplete( descriptor, ex );

            return;
        }

        try
        {
            observer.buildStarted( descriptor );

            File file = new File( basedir, "pom.xml" );

            MavenProject project = null;

            project = projectBuilder.build( file );

            List goals = new ArrayList();

            goals.add( "clean:clean" );

            goals.add( "jar:install" );

            // TODO: get the output from the maven build
            maven.execute( project, goals );

            // TODO: is this wanted?
            FileUtils.forceDelete( basedir );

            observer.buildComplete( descriptor, null );
        }
        catch ( ProjectBuildingException ex )
        {
            observer.buildComplete( descriptor, ex );

            return;
        }
        catch ( GoalNotFoundException ex )
        {
            observer.buildComplete( descriptor, ex );

            return;
        }
        catch ( IOException ex )
        {
            observer.buildComplete( descriptor, ex );

            return;
        }
    }
/*
    private List compileProject( MavenProject project )
        throws Exception
    {
        getLogger().info( "Done checking out the project!" );

        String destinationDirectory = buildDirectory + "/target/classes";
//        build.getProject().setProperty( "basedir", buildDirectory );

        List messages = compiler.compile( classpathElements( project ),
                                          new String[]{project.getBuild().getSourceDirectory()},
                                          destinationDirectory );

        getLogger().info( "Done compiling!" );

        return messages;
    }
*/

    private void assertConfiguration( Object configuration, String name )
        throws PlexusConfigurationException
    {
        if( configuration == null )
            throw new PlexusConfigurationException( "Missing configuration element: '" + name + "'." );
    }

    private void assertRequirement( Object requirement, Class clazz )
        throws PlexusConfigurationException
    {
        if ( requirement == null )
            throw new PlexusConfigurationException( "Missing requirement '" + clazz.getName() + "'." );
    }
}