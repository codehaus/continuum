package org.codehaus.continuum.builder.maven2;

/*
 * LICENSE
 */

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.maven.GoalNotFoundException;
import org.apache.maven.Maven;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingException;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.builder.ContinuumBuilder;
import org.codehaus.continuum.notification.ContinuumNotifier;
import org.codehaus.continuum.notification.NotifierManager;
import org.codehaus.continuum.project.BuildResult;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ProjectDescriptor;
import org.codehaus.continuum.scm.ContinuumScm;
import org.codehaus.continuum.store.ContinuumStore;
import org.codehaus.continuum.store.ContinuumStoreException;
import org.codehaus.continuum.utils.PlexusUtils;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.util.FileUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: Maven2ContinuumBuilder.java,v 1.4 2004-07-07 02:34:34 trygvis Exp $
 */
public class Maven2ContinuumBuilder
    extends AbstractLogEnabled
    implements ContinuumBuilder, Initializable, Startable
{
    /** @requirement */
    private Maven maven;

    /** @requirement */
    private ContinuumScm scm;

    /** @requirement */
    private NotifierManager notifier;

    /** @requirement */
    private ContinuumStore store;

    /** @configuration */
    private String checkoutDirectory;

    /** @default ${maven.home} */
    private String mavenHome;

    /** @default ${maven.home}/repository */
    private String mavenLocalRepository;

    private boolean mavenInitialized;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws Exception
    {
        PlexusUtils.assertRequirement( maven, Maven.ROLE );
        PlexusUtils.assertRequirement( scm, ContinuumScm.ROLE );
        PlexusUtils.assertRequirement( notifier, ContinuumNotifier.ROLE );
        PlexusUtils.assertRequirement( store, ContinuumStore.ROLE );
    }

    public void start()
        throws Exception
    {
        getLogger().info( "Using " + mavenHome + " as maven home." );

        getLogger().info( "Using " + mavenLocalRepository + " as the maven repository." );
    }

    public void stop()
        throws Exception
    {
    }

    // ----------------------------------------------------------------------
    // ContinuumBuilder implementation
    // ----------------------------------------------------------------------

    public ProjectDescriptor createDescriptor( ContinuumProject project )
    {
        Maven2ProjectDescriptor descriptor = new Maven2ProjectDescriptor();

        descriptor.getGoals().add( "clean:clean" );

        descriptor.getGoals().add( "jar:install" );

        return descriptor;
    }

    /**
     * This method does the actual building of a project.
     * 
     * @param descriptor
     * @throws ContinuumException
     */
    public synchronized void build( String buildId )
        throws ContinuumException
    {
        String basedir = null;

        BuildResult build;

        Maven2ProjectDescriptor descriptor;

        Maven maven = getMaven();

        try
        {
            build = store.getBuildResult( buildId );

            descriptor = (Maven2ProjectDescriptor) build.getProject().getDescriptor();
        }
        catch( ContinuumStoreException ex )
        {
            notifier.buildComplete( null, ex );

            setBuildResult( buildId, BuildResult.BUILD_RESULT_ERROR, ex );

            return;
        }

        ContinuumProject continuumProject = build.getProject();

        notifier.buildStarted( build );

        try
        {
            notifier.checkoutStarted( build );

            scm.clean( continuumProject );

            basedir = scm.checkout( continuumProject );

            notifier.checkoutComplete( build, null );
        }
        catch( Exception ex )
        {
            notifier.checkoutComplete( build, ex );

            setBuildResult( buildId, BuildResult.BUILD_RESULT_ERROR, ex );

            return;
        }

        try
        {
            notifier.buildStarted( build );

            File file = new File( basedir, "pom.xml" );

            MavenProject pom;

            pom = maven.getProject( file );

            List goals = descriptor.getGoals();

            // TODO: get the output from the maven build and check for error
            maven.execute( pom, goals );

            // TODO: is this wanted?
            FileUtils.forceDelete( basedir );

            notifier.buildComplete( build, null );

            setBuildResult( buildId, BuildResult.BUILD_RESULT_OK, null );
        }
        catch ( ProjectBuildingException ex )
        {
            notifier.buildComplete( build, ex );

            setBuildResult( buildId, BuildResult.BUILD_RESULT_ERROR, ex );

            return;
        }
        catch ( GoalNotFoundException ex )
        {
            notifier.buildComplete( build, ex );

            setBuildResult( buildId, BuildResult.BUILD_RESULT_ERROR, ex );

            return;
        }
        catch ( IOException ex )
        {
            notifier.buildComplete( build, ex );

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

    private Maven getMaven()
        throws ContinuumException
    {
        synchronized( maven )
        {
            if ( !mavenInitialized )
            {
                maven.setMavenHome( mavenHome );
        
                maven.setLocalRepository( mavenLocalRepository );
        
                getLogger().info( "Booting maven." );
    
                try
                {
                    maven.booty();
                }
                catch( Exception ex )
                {
                    throw new ContinuumException( "Cound not initialize Maven.", ex);
                }

                mavenInitialized = true;
            }
        }

        return maven;
    }
}
