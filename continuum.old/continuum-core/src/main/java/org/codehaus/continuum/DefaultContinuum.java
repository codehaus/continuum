package org.codehaus.continuum;

/*
 * LICENSE
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;

import org.codehaus.continuum.builder.ContinuumBuilder;
import org.codehaus.continuum.buildqueue.BuildQueue;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.projectstorage.ContinuumProjectStorage;
import org.codehaus.continuum.projectstorage.ContinuumProjectStorageException;
import org.codehaus.continuum.utils.PlexusUtils;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultContinuum.java,v 1.29 2004-06-27 23:21:03 trygvis Exp $
 */
public class DefaultContinuum
    extends AbstractLogEnabled
    implements Continuum, Initializable, Startable
{
    // configuration

    private String mavenHome;

    // requirements

    private ContinuumBuilder builder;

    private BuildQueue buildQueue;

    private MavenProjectBuilder projectBuilder;

//    private Maven maven;

    private ContinuumProjectStorage projectStorage;

    private boolean shutdown;

    ///////////////////////////////////////////////////////////////////////////
    // Component lifecycle

    public void initialize()
        throws Exception
    {
        getLogger().info( "Initializing continuum." );

        PlexusUtils.assertRequirement( builder, ContinuumBuilder.class );
        PlexusUtils.assertRequirement( buildQueue, BuildQueue.class );
        PlexusUtils.assertRequirement( projectBuilder, MavenProjectBuilder.class );
        PlexusUtils.assertRequirement( projectStorage, ContinuumProjectStorage.class );

        getLogger().info( "Continuum initialized." );
    }

    public void start()
        throws Exception
    {
        getLogger().info( "Starting continuum." );

        PlexusUtils.assertConfiguration( mavenHome, "mavenHome" );

        // start the builder thread
/*
        Thread thread = new Thread( new BuilderThread() );
        thread.setDaemon( true );
        thread.start();
*/
        getLogger().info( "Continuum started." );
    }

    public void stop()
        throws Exception
    {
/*
        int maxSleep = 10 * 1000; // 10 seconds
        int interval = 1000;
        int slept = 0;
*/
        getLogger().info( "Stopping continuum." );

        // signal the thread to stop
        shutdown = true;
/*
        while( getState() != ContinuumConstants.IDLE )
        {
            if ( slept > maxSleep )
            {
                getLogger().warn( "Timeout, stopping continuum." );
                break;
            }

            getLogger().info( "Waiting until continuum is idling..." );

            Thread.sleep( interval );

            slept += interval;
        }
*/
        getLogger().info( "Continuum stopped." );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Continuum implementation

    public String buildProject( String id )
        throws ContinuumException
    {
        String buildId;

        ContinuumProject project = getProject( id );

        try
        {
            buildId = projectStorage.createBuild( project );
        }
        catch( ContinuumProjectStorageException ex )
        {
            throw new ContinuumException( "Exception while creating build object.", ex );
        }

        getLogger().info( "Enqueuing " + project.getMavenProject().getName() + ", build id " + buildId + "..." );

        return buildId;
    }
/*
    public List buildProjects()
        throws ContinuumException
    {
        getLogger().info( "Building all projects ..." );

        List ids = new ArrayList();

        for ( Iterator i = builds.values().iterator(); i.hasNext(); )
        {
            MavenProject project = (MavenProject) i.next();

            ids.add( buildQueue.enqueue( project ) );
        }

        return ids;
    }
/*
    public int getState()
    {
        if ( building || addingProject )
            return ContinuumConstants.WORKING;

        return ContinuumConstants.IDLE;
    }
*/
    /**
     * Returns the current length of the build queue.
     * 
     * @return Returns the current length of the build queue.
     */
    public int getBuildQueueLength()
        throws ContinuumException
    {
        return buildQueue.getLength();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Private
/*
    private class BuilderThread
        implements Runnable
    {
        public void run()
        {
            while ( !shutdown )
            {
                MavenProject project = getProject();

                if( project == null )
                {
                    getLogger().info( "Builder sleeping..." );

                    sleep( 1000 );

                    continue;
                }

                builder.build( project );
            }
        }

        private void sleep( int interval )
        {
            try
            {
                Thread.sleep( interval );
            }
            catch( InterruptedException ex )
            {
                // ignore
            }
        }

        private MavenProject getProject()
        {
            try
            {
                return buildQueue.dequeue();
            }
            catch( ContinuumException ex )
            {
                getLogger().fatalError( "Exception while dequeueing project.", ex );

                return null;
            }
        }
    }
*/
    public String addProject( MavenProject project )
        throws ContinuumException
    {
        String id;

        try
        {
            id = projectStorage.storeProject( project );

            getLogger().info( "Added project: " + project.getName() );
        }
        catch ( Exception ex )
        {
            getLogger().error( "Cannot add project!", ex );

            throw new ContinuumException( "Exception while building project.", ex );
        }

        return id;
    }
/*
    private boolean hasProject( String groupId, String artifactId )
    {
        return builds.containsKey( createId( groupId, artifactId ) );
    }
*/
    private ContinuumProject getProject( String id )
        throws ContinuumException
    {
        try
        {
            ContinuumProject project = projectStorage.getProject( id );
    
            if ( project == null )
                throw new ContinuumException( "No such project: " + id + "." );
    
            return project;
        }
        catch( ContinuumProjectStorageException ex )
        {
            throw new ContinuumException( "Could not retrieve project #" + id, ex );
        }
    }
}
