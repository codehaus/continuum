package org.codehaus.continuum;

/*
 * Copyright (c) 2004, Jason van Zyl and Trygve Laugstøl
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.util.Iterator;

import org.codehaus.continuum.builder.BuilderManager;
import org.codehaus.continuum.builder.ContinuumBuilder;
import org.codehaus.continuum.buildqueue.BuildQueue;
import org.codehaus.continuum.notification.NotifierManager;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ProjectDescriptor;
import org.codehaus.continuum.scm.ContinuumScm;
import org.codehaus.continuum.store.ContinuumStore;
import org.codehaus.continuum.store.ContinuumStoreException;
import org.codehaus.continuum.utils.PlexusUtils;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultContinuum.java,v 1.40 2004-09-07 16:22:16 trygvis Exp $
 */
public class DefaultContinuum
    extends AbstractLogEnabled
    implements Continuum, Initializable, Startable, Contextualizable
{
    /** @requirement */
    private BuilderManager builderManager;

    /** @requirement */
    private BuildQueue buildQueue;

    /** @requirement */
    private ContinuumScm scm;

    /** @requirement */
    private NotifierManager notifierManager;

    /** @requirement */
    private ContinuumStore store;

    /** @configuration */
    private String checkoutDirectory;

    private BuilderThread builderThread;

    // ----------------------------------------------------------------------
    // Component lifecycle
    // ----------------------------------------------------------------------

    String plexusHome;
    DefaultPlexusContainer container;

    public void contextualize( Context context )
        throws Exception
    {
        plexusHome = (String)context.get( "plexus.home" );

        container = (DefaultPlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }

    public void initialize()
        throws Exception
    {
        getLogger().info( "Initializing continuum." );

        PlexusUtils.assertRequirement( builderManager, BuilderManager.ROLE );
        PlexusUtils.assertRequirement( buildQueue, BuildQueue.ROLE );
        PlexusUtils.assertRequirement( store, ContinuumStore.ROLE );
        PlexusUtils.assertRequirement( scm, ContinuumScm.ROLE );
        PlexusUtils.assertRequirement( notifierManager, NotifierManager.ROLE );

        getLogger().info( "Showing all projects:" );

        for ( Iterator it = store.getAllProjects(); it.hasNext(); )
        {
            ContinuumProject project = (ContinuumProject) it.next();

            getLogger().info( " " + project.getId() + ":" + project.getName() + ":" + project.getType() );
        }
    }

    public void start()
        throws Exception
    {
        getLogger().info( "Starting continuum." );

        if ( container.hasComponent( "org.codehaus.continuum.builder.ContinuumBuilder", "maven2" ) )
        {
            getLogger().warn( "Has m2 builder, booting" );

            container.lookup( "org.codehaus.continuum.builder.ContinuumBuilder", "maven2" );
        }
        else
        {
            getLogger().warn( "Missing m2 builder" );
        }
/*
        if ( !started )
        {
            File jarRepo = new File( plexusHome, "/apps/continuumweb/plugins" );

            if ( !jarRepo.exists() )
            {
                getLogger().warn( "JAR REPOSITORY DOESN'T EXIST: " + jarRepo.getAbsolutePath() );
            }
            else
            {
                getLogger().warn( "JAR REPOSITORY: " + jarRepo.getAbsolutePath() );

                // this initializes maven
                container.lookup( "org.codehaus.continuum.builder.ContinuumBuilder", "maven2" );

                container.addJarRepository( jarRepo );

                container.discoverComponents();
            }
            started = true;
        }
*/
        // start the builder thread
        builderThread = new BuilderThread( builderManager, buildQueue, store, notifierManager, scm, getLogger() );

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

    // ----------------------------------------------------------------------
    // Continuum Implementation
    // ----------------------------------------------------------------------

    public String addProject( String name, String scmConnection, String type )
        throws ContinuumException
    {
        String projectId;

        try
        {
            store.beginTransaction();

            projectId = store.addProject( name, scmConnection, type );

            ContinuumProject project = store.getProject( projectId );

            ContinuumBuilder builder = builderManager.getBuilderForProject( projectId );

            ProjectDescriptor descriptor = builder.createDescriptor( project );

            store.setProjectDescriptor( projectId, descriptor );

            store.commitTransaction();

            getLogger().info( "Added project: " + name );
        }
        catch ( Exception ex )
        {
            rollback();

            getLogger().error( "Exception while building project.", ex );

            throw new ContinuumException( "Exception while building project.", ex );
        }

        return projectId;
    }

    public Iterator getAllProjects( int start, int end )
        throws ContinuumException
    {
        try
        {
            return store.getAllProjects();
        }
        catch( ContinuumStoreException ex )
        {
            throw new ContinuumException( "Exception while getting all projects.", ex );
        }
    }

    public String buildProject( String id )
        throws ContinuumException
    {
        String buildId;

        ContinuumProject project = getProject( id );

        try
        {
            store.beginTransaction();

            buildId = store.createBuild( project.getId() );

            getLogger().info( "Enqueuing " + project.getName() + ", projet id: " + project.getId() + ", build id: " + buildId + "..." );

            store.commitTransaction();

            buildQueue.enqueue( buildId );
        }
        catch( ContinuumStoreException ex )
        {
            rollback();

            getLogger().error( "Exception while building project.", ex );

            throw new ContinuumException( "Exception while creating build object.", ex );
        }

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

    // ----------------------------------------------------------------------
    // Private
    // ----------------------------------------------------------------------

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

    private void rollback()
    {
        try
        {
            store.rollbackTransaction();
        }
        catch( Exception ex )
        {
            getLogger().warn( "Exception while rolling back transaction, ignored.", ex );
        }
    }
}
