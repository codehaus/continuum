package org.codehaus.continuum.builder.maven2;

/*
 * LICENSE
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.GoalNotFoundException;
import org.apache.maven.Maven;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingException;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.builder.ContinuumBuilder;
import org.codehaus.continuum.notification.ContinuumNotifier;
import org.codehaus.continuum.notification.NotifierWrapper;
import org.codehaus.continuum.project.BuildResult;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.scm.ContinuumScm;
import org.codehaus.continuum.store.ContinuumStore;
import org.codehaus.continuum.store.ContinuumStoreException;
import org.codehaus.continuum.utils.PlexusUtils;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

/**
 * @component.role org.codehaus.continuum.builder.ContinuumBuilder
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: Maven2ContinuumBuilder.java,v 1.3 2004-07-03 03:21:14 trygvis Exp $
 */
public class Maven2ContinuumBuilder
    extends AbstractLogEnabled
    implements Initializable, Startable , ContinuumBuilder
{
    /** @requirement */
    private Maven maven;

    /** @requirement */
    private ContinuumScm scm;

    /** @requirement */
    private ContinuumNotifier notifier;

    /** @requirement */
    private ContinuumStore store;

    /** @configuration */
    private String checkoutDirectory;

    /** @default ${maven.home} */
    private String mavenHome;

    /** @default ${maven.home}/repository */
    private String mavenLocalRepository;

    private NotifierWrapper observer;

    ///////////////////////////////////////////////////////////////////////////
    // Lifecycle

    public void initialize()
        throws Exception
    {
        PlexusUtils.assertRequirement( maven, Maven.ROLE );
        PlexusUtils.assertRequirement( scm, ContinuumScm.ROLE );
//        PlexusUtils.assertRequirement( projectBuilder, MavenProjectBuilder.ROLE );
        PlexusUtils.assertRequirement( notifier, ContinuumNotifier.ROLE );
        PlexusUtils.assertRequirement( store, ContinuumStore.ROLE );

        observer = new NotifierWrapper( notifier, getLogger() );
    }
    
    public void start()
        throws Exception
    {
        getLogger().info( "Using " + mavenHome + " as maven home." );

        maven.setMavenHome( mavenHome );

        getLogger().info( "Using " + mavenLocalRepository + " as the maven repository." );

        maven.setLocalRepository( mavenLocalRepository );

        getLogger().info( "Booting maven." );

        maven.booty();
    }

    public void stop()
        throws Exception
    {
    }

    ///////////////////////////////////////////////////////////////////////////
    // ContinuumBuilder implementation

    /**
     * This method does the actual building of a project.
     * 
     * @param descriptor
     * @throws ContinuumException
     */
    public synchronized void build( String buildId )
    {
        String basedir = null;

        ContinuumProject continuumProject;

        BuildResult build;

        try
        {
            build = store.getBuildResult( buildId );

            continuumProject = store.getProject( build.getProjectId() );
        }
        catch( ContinuumStoreException ex )
        {
            observer.buildComplete( null, ex );

            setBuildResult( buildId, BuildResult.BUILD_RESULT_ERROR, ex );

            return;
        }

        observer.buildStarted( build );

        try
        {
            observer.checkoutStarted( build );

            String[] connection = StringUtils.split( continuumProject.getScmConnection(), ":" );

            if ( connection.length != 6 )
                throw new ContinuumException( "Invalid connection string." );

            scm.clean( continuumProject );

            basedir = scm.checkout( continuumProject );

            observer.checkoutComplete( build, null );
        }
        catch( Exception ex )
        {
            observer.checkoutComplete( build, ex );

            setBuildResult( buildId, BuildResult.BUILD_RESULT_ERROR, ex );

            return;
        }

        try
        {
            observer.buildStarted( build );

            File file = new File( basedir, "pom.xml" );

            MavenProject pom;

            pom = maven.getProject( file );

            List goals = new ArrayList();

            goals.add( "clean:clean" );

            goals.add( "jar:install" );

            // TODO: get the output from the maven build and check for error
            maven.execute( pom, goals );

            // TODO: is this wanted?
            FileUtils.forceDelete( basedir );

            observer.buildComplete( build, null );

            setBuildResult( buildId, BuildResult.BUILD_RESULT_OK, null );
        }
        catch ( ProjectBuildingException ex )
        {
            observer.buildComplete( build, ex );

            setBuildResult( buildId, BuildResult.BUILD_RESULT_ERROR, ex );

            return;
        }
        catch ( GoalNotFoundException ex )
        {
            observer.buildComplete( build, ex );

            setBuildResult( buildId, BuildResult.BUILD_RESULT_ERROR, ex );

            return;
        }
        catch ( IOException ex )
        {
            observer.buildComplete( build, ex );

            setBuildResult( buildId, BuildResult.BUILD_RESULT_ERROR, ex );

            return;
        }
    }

    private void setBuildResult( String buildId, int state, Exception ex )
    {
        try
        {
            store.setBuildResult( buildId, state, ex );
        }
        catch( ContinuumStoreException ex2 )
        {
            getLogger().fatalError( "Error while persising build state.", ex2 );
        }
    }
}
