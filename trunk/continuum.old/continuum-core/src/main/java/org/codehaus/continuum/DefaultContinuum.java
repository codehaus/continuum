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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import org.apache.maven.project.MavenProject;

import org.codehaus.continuum.buildcontroller.BuildController;
import org.codehaus.continuum.builder.BuilderManager;
import org.codehaus.continuum.builder.ContinuumBuilder;
import org.codehaus.continuum.buildqueue.BuildQueue;
import org.codehaus.continuum.maven.MavenTool;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ProjectDescriptor;
import org.codehaus.continuum.scm.ContinuumScm;
import org.codehaus.continuum.scm.ContinuumScmException;
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
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l </a>
 * @version $Id: DefaultContinuum.java,v 1.50 2004-10-29 15:18:12 trygvis Exp $
 */
public class DefaultContinuum
    extends AbstractLogEnabled
    implements Continuum, Initializable, Startable, Contextualizable
{
    /** @requirement */
    private BuilderManager builderManager;

    /** @requirement */
    private BuildController buildController;

    /** @requirement */
    private BuildQueue buildQueue;

    /** @requirement */
    private ContinuumStore store;

    /** @requirement */
    private StoreTransactionManager txManager;

    /** @requirement */
    private ContinuumScm scm;

    /** @requirement */
    private MavenTool mavenTool;

    /** @configuration */
    private String workingDirectory;

    private final static String continuumVersion = "1.0-alpha-1-SNAPSHOT";

    private BuilderThread builderThread;

    private Thread builderThreadThread;

    // ----------------------------------------------------------------------
    // Component lifecycle
    // ----------------------------------------------------------------------

    String plexusHome;

    DefaultPlexusContainer container;

    public void contextualize( Context context )
    	throws Exception
    {
        plexusHome = (String) context.get( "plexus.home" );

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
        PlexusUtils.assertRequirement( mavenTool, MavenTool.ROLE );

        PlexusUtils.assertConfiguration( workingDirectory, "working-directory" );

        File wdFile = new File( workingDirectory );

        if ( wdFile.exists() )
        {
            if ( !wdFile.isDirectory() )
            {
                throw new ContinuumException( "The specified working directory isn't a directory: " + wdFile.getAbsolutePath() );
            }
        }
        else
        {
            if ( !wdFile.mkdirs() )
            {
                throw new ContinuumException( "Could not making the working directory: " + wdFile.getAbsolutePath() );
            }
        }

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
        builderThread = new BuilderThread( buildController, buildQueue, getLogger() );

        builderThreadThread = new Thread( builderThread );

        builderThreadThread.setDaemon( true );

        builderThreadThread.start();

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

        builderThreadThread.interrupt();

        while ( !builderThread.isDone() )
        {
            if ( slept > maxSleep )
            {
                getLogger().warn( "Timeout, stopping continuum." );

                break;
            }

            getLogger().info( "Waiting until continuum is idling..." );

            try
            {
                synchronized ( builderThread )
                {
                    builderThread.wait( interval );
                }
            }
            catch ( InterruptedException ex )
            {
                // ignore
            }

            // TODO: should use System.currentTimeMillis()
            slept += interval;
        }

        getLogger().info( "Continuum stopped." );
    }

    // ----------------------------------------------------------------------
    // Continuum Implementation
    // ----------------------------------------------------------------------

    public String addProjectFromScm( String scmUrl, String builderType )
        throws ContinuumException
    {
        File coDir = new File( workingDirectory, "temp-project" );

        if ( coDir.exists() )
        {
            try
            {
                FileUtils.cleanDirectory( coDir );
            }
            catch( IOException ex )
            {
                throw new ContinuumException( "Error while cleaning out " + coDir.getAbsolutePath() );
            }
        }
        else
        {
            if ( !coDir.mkdirs() )
            {
                throw new ContinuumException( "Could not make the check out directory (" + coDir.getAbsolutePath() + ")." );
            }
        }

        try
        {
            scm.checkOut( coDir, scmUrl );
        }
        catch( ContinuumScmException ex )
        {
            throw new ContinuumException( "Error while checking out the project.", ex );
        }

        ContinuumBuilder builder = builderManager.getBuilder( builderType );

        ContinuumProject project = builder.createProject( coDir );

        if ( project.getScmUrl() == null )
        {
            project.setScmUrl( scmUrl );
        }

        String projectId = addProject( project, builderType );

        return projectId;
    }

    public String addProjectFromUrl( URL url, String builder )
        throws ContinuumException
    {
        File pomFile;

        try
        {
            String pom = IOUtil.toString( url.openStream() );

            pomFile = File.createTempFile( "continuum-", "-pom-download" );

            FileUtils.fileWrite( pomFile.getAbsolutePath(), pom );
        }
        catch( IOException ex )
        {
            throw new ContinuumException( "Error while downloading the pom.", ex );
        }

        MavenProject project = mavenTool.getProject( pomFile );

        return addProjectFromScm( mavenTool.getScmUrl( project ), builder );
    }

    public void updateProject( String projectId ) throws ContinuumException
    {
        try
        {
            txManager.enter();

            ContinuumProject project = store.getProject( projectId );

            ContinuumBuilder builder = builderManager.getBuilderForProject( projectId );

            project = builder.createProject( new File( project.getWorkingDirectory() ) );

            store.updateProject( projectId, project.getName(), project.getScmUrl(), project.getNagEmailAddress(), project.getVersion() );

            store.updateProjectDescriptor( projectId, project.getDescriptor() );

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

    public void removeProject( String projectId ) throws ContinuumException
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

    public ContinuumProject getProject( String projectId ) throws ContinuumException
    {
        try
        {
            txManager.enter();

            ContinuumProject project = store.getProject( projectId );

            txManager.leave();

            return project;
        }
        catch ( ContinuumStoreException ex )
        {
            txManager.rollback();

            getLogger().error( "Error while finding all projects.", ex );

            throw new ContinuumException( "Exception while getting all projects.", ex );
        }
    }

    public Iterator getAllProjects( int start, int end ) throws ContinuumException
    {
        try
        {
            txManager.enter();

            Iterator it = store.getAllProjects();

            txManager.leave();

            return it;
        }
        catch ( ContinuumStoreException ex )
        {
            txManager.rollback();

            getLogger().error( "Error while finding all projects.", ex );

            throw new ContinuumException( "Exception while getting all projects.", ex );
        }
    }

    // This method cannot be run inside another transaction
    // because the tx has to be committed before it's put on the build queue
    public String buildProject( String id ) throws ContinuumException
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
        catch ( ContinuumStoreException ex )
        {
            txManager.rollback();

            getLogger().error( "Exception while building project.", ex );

            throw new ContinuumException( "Exception while creating build object.", ex );
        }
    }

    public boolean checkIfProjectNeedsToBeBuilt( String projectId )
        throws ContinuumException
    {
        try
        {
            txManager.enter();

            ContinuumProject project = store.getProject( projectId );

            txManager.leave();

            boolean needsBuild = scm.updateProject( project );

            if ( needsBuild )
            {
                getLogger().info( "Project needs to be built: " + project.getName() );

                buildProject( projectId );
            }

            return needsBuild;
        }
        catch ( ContinuumScmException ex )
        {
            txManager.rollback();

            throw new ContinuumException( "Exception while checking the project.", ex );
        }
        catch ( ContinuumStoreException ex )
        {
            txManager.rollback();

            throw new ContinuumException( "Exception while checking the project.", ex );
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

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private String addProject( ContinuumProject project, String builderType )
        throws ContinuumException
    {
        try
        {
            txManager.enter();

            Iterator it = store.findProjectsByName( project.getName() );

            txManager.leave();

            if ( it.hasNext() )
            {
                ContinuumProject tmpProject = (ContinuumProject) it.next();

                updateProject( tmpProject.getId() );

                return tmpProject.getId();
            }
            else
            {
                txManager.enter();

                // ----------------------------------------------------------------------
                // Store the project
                // ----------------------------------------------------------------------

                String projectId = store.addProject( project.getName(), 
                                                     project.getScmUrl(),
                                                     project.getNagEmailAddress(),
                                                     project.getVersion(),
                                                     builderType );

                // TODO: Use the groupId + artifactId here
                File projectWorkingDirectory = new File( workingDirectory, project.getName().replace( ' ', '-' ) );

                if ( !projectWorkingDirectory.exists() && !projectWorkingDirectory.mkdirs() )
                {
                    throw new ContinuumException( "Could not make the working directory for the project (" + projectWorkingDirectory.getAbsolutePath() + ")." );
                }

                store.setWorkingDirectory( projectId, projectWorkingDirectory.getAbsolutePath() );

                ProjectDescriptor descriptor = project.getDescriptor();

                project = store.getProject( projectId );

                System.err.println( "descriptor: " + descriptor );

                store.setProjectDescriptor( projectId, descriptor );

                txManager.leave();

                getLogger().info( "Added project: " + project.getName() );
                getLogger().info( "  Working directory: " + project.getWorkingDirectory() );

                scm.checkOutProject( project );

                return projectId;
            }
        }
        catch ( ContinuumScmException ex )
        {
            txManager.rollback();

            getLogger().error( "Exception while checking out the project.", ex );

            throw new ContinuumException( "Exception while checking out the project.", ex );
        }
        catch ( ContinuumStoreException ex )
        {
            txManager.rollback();

            getLogger().error( "Exception while adding project.", ex );

            throw new ContinuumException( "Exception while adding project.", ex );
        }
    }
}
