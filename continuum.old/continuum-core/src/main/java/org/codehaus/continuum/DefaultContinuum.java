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
import org.codehaus.continuum.store.tx.StoreTransactionManager;
import org.codehaus.continuum.utils.PlexusUtils;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultContinuum.java,v 1.44 2004-10-09 13:01:52 trygvis Exp $
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

    /** @requirement */
    private StoreTransactionManager txManager;

    private final static String continuumVersion = "1.0-alpha-1-SNAPSHOT";

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
        PlexusUtils.assertRequirement( txManager, StoreTransactionManager.ROLE );
        PlexusUtils.assertRequirement( scm, ContinuumScm.ROLE );
        PlexusUtils.assertRequirement( notifierManager, NotifierManager.ROLE );

        getLogger().info( "Showing all projects: " );

        txManager.begin();

        for ( Iterator it = store.getAllProjects(); it.hasNext(); )
        {
            ContinuumProject project = (ContinuumProject) it.next();

            getLogger().info( " " + project.getId() + ":" + project.getName() + ":" + project.getType() );
        }

        txManager.commit();
    }

    public void start()
        throws Exception
    {
        getLogger().info( "Starting continuum." );

        // start the builder thread
        builderThread = new BuilderThread( builderManager, buildQueue, store, txManager, notifierManager, scm, getLogger() );

        Thread thread = new Thread( builderThread );

        thread.setDaemon( true );

        thread.start();

        String banner = StringUtils.repeat( "-", continuumVersion.length() );

        getLogger().info( "" );
        getLogger().info( "" );
        getLogger().info( "< Continuum " + continuumVersion + " started! >" );
        getLogger().info( "-----------------------" + banner );
        getLogger().info( "       \\   ^__^" );
        getLogger().info( "        \\  (oo)\\_______" );
        getLogger().info( "           (__)\\       )\\/\\" );
        getLogger().info( "               ||----w |" );
        getLogger().info( "               ||     ||" );
        getLogger().info( "" );
        getLogger().info( "" );
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
        try
        {
            txManager.enter();

            Iterator it = store.findProjectsByName( name );

            txManager.leave();

            if ( it.hasNext() )
            {
                ContinuumProject project = (ContinuumProject) it.next();

                updateProject( project.getId() );

                return project.getId();
            }
            else
            {
                txManager.enter();

                String projectId = store.addProject( name, scmConnection, type );

                ContinuumProject project = store.getProject( projectId );

                ContinuumBuilder builder = builderManager.getBuilderForProject( projectId );

                ProjectDescriptor descriptor = builder.createDescriptor( project );

                store.updateProject( projectId, project.getName(), project.getScmConnection() );

                store.setProjectDescriptor( projectId, descriptor );

                txManager.leave();

                getLogger().info( "Added project: " + project.getName() );

                return projectId;
            }
        }
        catch ( ContinuumStoreException ex )
        {
            txManager.rollback();

            getLogger().error( "Exception while adding project.", ex );

            throw new ContinuumException( "Exception while adding project.", ex );
        }
    }

    public void updateProject( String projectId )
        throws ContinuumException
    {
        try
        {
            txManager.enter();

            ContinuumProject project = store.getProject( projectId );

            ContinuumBuilder builder = builderManager.getBuilderForProject( projectId );

            ProjectDescriptor descriptor = builder.createDescriptor( project );

            store.updateProject( projectId, project.getName(), project.getScmConnection() );

            store.updateProjectDescriptor( projectId, descriptor );

            txManager.leave();

            getLogger().info( "Updated project: " + project.getName() );
        }
        catch ( ContinuumStoreException ex )
        {
            txManager.rollback();

            getLogger().error( "Error while updating project.", ex );

            throw new ContinuumException( "Error while updating project.", ex );
        }
    }

    public void removeProject( String projectId )
        throws ContinuumException
    {
        try
        {
            txManager.enter();

            store.removeProject( projectId );

            txManager.leave();
        }
        catch ( ContinuumStoreException ex )
        {
            txManager.rollback();

            getLogger().error( "Error while updating project.", ex );

            throw new ContinuumException( "Error while removing project.", ex );
        }
    }

    public ContinuumProject getProject( String projectId )
        throws ContinuumException
    {
        try
        {
            txManager.enter();

            ContinuumProject project = store.getProject( projectId );

            txManager.leave();

            return project;
        }
        catch( ContinuumStoreException ex )
        {
            txManager.rollback();

            getLogger().error( "Error while finding all projects.", ex );

            throw new ContinuumException( "Exception while getting all projects.", ex );
        }
    }

    public Iterator getAllProjects( int start, int end )
        throws ContinuumException
    {
        try
        {
            txManager.enter();

            Iterator it = store.getAllProjects();

            txManager.leave();

            return it;
        }
        catch( ContinuumStoreException ex )
        {
            txManager.rollback();

            getLogger().error( "Error while finding all projects.", ex );

            throw new ContinuumException( "Exception while getting all projects.", ex );
        }
    }

    // This method cannot be run inside another transaction
    // because the tx has to be committed before it's put on the build queue
    public String buildProject( String id )
        throws ContinuumException
    {
        try
        {
            txManager.begin();

            ContinuumProject project = store.getProject( id );

            String buildId = store.createBuild( project.getId() );

            txManager.commit();

            getLogger().info( "Enqueuing " + project.getName() + ", projet id: " + project.getId() + ", build id: " + buildId + "..." );

            buildQueue.enqueue( buildId );

            return buildId;
        }
        catch( ContinuumStoreException ex )
        {
            txManager.rollback();

            getLogger().error( "Exception while building project.", ex );

            throw new ContinuumException( "Exception while creating build object.", ex );
        }
    }

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
}
