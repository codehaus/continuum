package org.codehaus.continuum;

/*
 * LICENSE
 */

import org.codehaus.continuum.builder.ContinuumBuilder;
import org.codehaus.continuum.buildqueue.BuildQueue;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.store.ContinuumStore;
import org.codehaus.continuum.store.ContinuumStoreException;
import org.codehaus.continuum.utils.PlexusUtils;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultContinuum.java,v 1.31 2004-07-03 03:21:13 trygvis Exp $
 */
public class DefaultContinuum
    extends AbstractLogEnabled
    implements Continuum, Initializable, Startable
{
    private ContinuumBuilder builder;

    private BuildQueue buildQueue;

    private ContinuumStore store;

    private BuilderThread builderThread;

    ///////////////////////////////////////////////////////////////////////////
    // Component lifecycle

    public void initialize()
        throws Exception
    {
        getLogger().info( "Initializing continuum." );

        PlexusUtils.assertRequirement( builder, ContinuumBuilder.ROLE );

        PlexusUtils.assertRequirement( buildQueue, BuildQueue.ROLE );

        PlexusUtils.assertRequirement( store, ContinuumStore.ROLE );

        getLogger().info( "Continuum initialized." );
    }

    public void start()
        throws Exception
    {
        getLogger().info( "Starting continuum." );

        // start the builder thread
        builderThread = new BuilderThread( buildQueue, getLogger(), builder );

        Thread thread = new Thread( builderThread );

        thread.setDaemon( true );

        thread.start();

        getLogger().info( "Continuum started." );
    }

    public void stop()
        throws Exception
    {
        int maxSleep = 10 * 1000; // 10 seconds
        int interval = 1000;
        int slept = 0;

        getLogger().info( "Stopping continuum." );

        // signal the thread to stop
        builderThread.shutdown();

        while( builderThread.isDone() )
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

        getLogger().info( "Continuum stopped." );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Continuum implementation

    public String addProject( String name, String scmConnection )
        throws ContinuumException
    {
        String id;

        try
        {
            id = store.addProject( name, scmConnection );

            getLogger().info( "Added project: " + name );
        }
        catch ( Exception ex )
        {
            getLogger().error( "Cannot add project!", ex );

            throw new ContinuumException( "Exception while building project.", ex );
        }

        return id;
    }

    public String buildProject( String id )
        throws ContinuumException
    {
        String buildId;

        ContinuumProject project = getProject( id );

        try
        {
            buildId = store.createBuildResult( project.getId() );

            buildQueue.enqueue( buildId );
        }
        catch( ContinuumStoreException ex )
        {
            throw new ContinuumException( "Exception while creating build object.", ex );
        }

        getLogger().info( "Enqueuing " + project.getName() + ", build id " + buildId + "..." );

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
            ContinuumProject project = store.getProject( id );

            if ( project == null )
                throw new ContinuumException( "No such project: " + id + "." );

            return project;
        }
        catch( ContinuumStoreException ex )
        {
            throw new ContinuumException( "Could not retrieve project #" + id, ex );
        }
    }
}
