package org.codehaus.continuum.builder.maven2;

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
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

/**
 * @component.role org.codehaus.continuum.builder.ContinuumBuilder
 * @c-omponent.roleHint maven2
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: Maven2ContinuumBuilder.java,v 1.1 2004-07-01 15:30:56 trygvis Exp $
 */
public class Maven2ContinuumBuilder
    extends AbstractLogEnabled
    implements Initializable, ContinuumBuilder
{
    /** @requirement */
    private MavenCore maven;

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
        PlexusUtils.assertRequirement( maven, MavenCore.ROLE );
        PlexusUtils.assertRequirement( scm, ContinuumScm.ROLE );
//        PlexusUtils.assertRequirement( projectBuilder, MavenProjectBuilder.ROLE );
        PlexusUtils.assertRequirement( notifier, ContinuumNotifier.ROLE );
        PlexusUtils.assertRequirement( store, ContinuumStore.ROLE );

        PlexusUtils.assertConfiguration( mavenHome, "maven-home" );

        getLogger().info( "Using " + mavenHome + " as maven home." );

        maven.setMavenHome( mavenHome );

        PlexusUtils.assertConfiguration( mavenLocalRepository, "maven-local-repo" );

        getLogger().info( "Using " + mavenLocalRepository + " as the maven repository." );

        maven.setLocalRepository( mavenLocalRepository );

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
    public synchronized void build( String buildId )
    {
        String basedir = null;

        ContinuumProject continuumProject;

        try
        {
            BuildResult build = store.getBuildResult( buildId );

            continuumProject = store.getProject( build.getProjectId() );
        }
        catch( ContinuumStoreException ex )
        {
            observer.buildComplete( null, ex );

            setBuildResult( buildId, BuildResult.BUILD_RESULT_ERROR, ex );

            return;
        }

        MavenProject project = continuumProject.getMavenProject();

        observer.buildStarted( project );

        try
        {
            observer.checkoutStarted( project );

            // scm:cvs:pserver:anonymous@cvs.codehaus.org:/scm/cvspublic:plexus/plexus-components/native/continuum/src/test-projects/project1

            String[] connection = StringUtils.split( project.getScm().getConnection(), ":" );

            if ( connection.length != 6 )
                throw new ContinuumException( "Invalid connection string." );

            if ( !connection[1].equals( "cvs" ) )
                throw new ContinuumException( "Continuum currently only supports 'cvs' as scm repo." );

            scm.clean( project );

            basedir = scm.checkout( project );

            observer.checkoutComplete( project, null );
        }
        catch( Exception ex )
        {
            observer.checkoutComplete( project, ex );

            setBuildResult( buildId, BuildResult.BUILD_RESULT_ERROR, ex );

            return;
        }

        try
        {
            observer.buildStarted( project );

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

            observer.buildComplete( project, null );

            setBuildResult( buildId, BuildResult.BUILD_RESULT_OK, null );
        }
        catch ( ProjectBuildingException ex )
        {
            observer.buildComplete( project, ex );

            setBuildResult( buildId, BuildResult.BUILD_RESULT_ERROR, ex );

            return;
        }
        catch ( GoalNotFoundException ex )
        {
            observer.buildComplete( project, ex );

            setBuildResult( buildId, BuildResult.BUILD_RESULT_ERROR, ex );

            return;
        }
        catch ( IOException ex )
        {
            observer.buildComplete( project, ex );

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
