package org.codehaus.continuum;

/*
 * The MIT License
 *
 * Copyright (c) 2004, Jason van Zyl and Trygve Laugst�l
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
import java.util.Properties;

import org.codehaus.continuum.buildcontroller.BuildController;
import org.codehaus.continuum.builder.ContinuumBuilder;
import org.codehaus.continuum.builder.manager.BuilderManager;
import org.codehaus.continuum.buildqueue.BuildQueue;
import org.codehaus.continuum.buildqueue.BuildQueueException;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.scm.ContinuumScm;
import org.codehaus.continuum.scm.ContinuumScmException;
import org.codehaus.continuum.store.ContinuumStore;
import org.codehaus.continuum.store.ContinuumStoreException;
import org.codehaus.continuum.utils.PlexusUtils;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l </a>
 * @version $Id: DefaultContinuum.java,v 1.6 2005-03-09 00:14:25 trygvis Exp $
 */
public class DefaultContinuum
    extends AbstractLogEnabled
    implements Continuum, Initializable, Startable
{
    private BuilderManager builderManager;

    private BuildController buildController;

    private BuildQueue buildQueue;

    private ContinuumStore store;

    private ContinuumScm scm;

    private String workingDirectory;

    private final static String continuumVersion = "1.0-alpha-1-SNAPSHOT";

    private BuilderThread builderThread;

    private Thread builderThreadThread;

    // ----------------------------------------------------------------------
    // Here it would probably be possible to tell from looking at the meta
    // data what type of project handler would be required. We could
    // definitely tell if we were looking at a Maven POM, So for the various
    // POM versions we would know what builder to use, and for an arbitrary
    // ----------------------------------------------------------------------

    // add project meta data
    // create continuum project from project metadata
    // add continuum project to the store
    // setup the project
    // -> check out from scm
    // -> update the project metadata

    public String addProjectFromUrl( URL url, String builderType )
        throws ContinuumException
    {
        File pomFile;

        try
        {
            String pom = IOUtil.toString( url.openStream() );

            pomFile = File.createTempFile( "continuum-", "-pom-download" );

            FileUtils.fileWrite( pomFile.getAbsolutePath(), pom );

            getLogger().info( "wrote pom to " + pomFile );
        }
        catch ( IOException ex )
        {
            throw new ContinuumException( "Error while downloading the pom.", ex );
        }

        // ----------------------------------------------------------------------
        // Really what we want to do is encapsulate the all handling to a builderType
        // or project type handler to deal with everything. Take the initial
        // URL which points to the metadata for the project and let the
        // handler deal with everything else.
        // ----------------------------------------------------------------------

        ContinuumBuilder builder = builderManager.getBuilder( builderType );

        getLogger().info( "We have the builder: " + builder );

        ContinuumProject project = builder.createProjectFromMetadata( url );

        getLogger().info( "done creating continuum project" );

        // TODO: Update from metadata in the initial checkout?

        project = addProjectAndCheckOutSources( project, builderType );

        return project.getId();
    }

    public String addProjectFromScm( String scmUrl, String builderType, String projectName, String nagEmailAddress,
                                     String version, Properties configuration )
        throws ContinuumException
    {
        // ----------------------------------------------------------------------
        // Create the stub project
        // ----------------------------------------------------------------------

        ContinuumProject project = new ContinuumProject();

        project.setScmUrl( scmUrl );

        project.setBuilderId( builderType );

        project.setName( projectName );

        project.setNagEmailAddress( nagEmailAddress );

        project.setVersion( version );

        project.setConfiguration( configuration );

        // ----------------------------------------------------------------------
        // Make sure that the builder id is correct before starting to check
        // stuff out
        // ----------------------------------------------------------------------

        if( !builderManager.hasBuilder( builderType ) )
        {
            throw new ContinuumException( "No such builder '" + builderType + "'." );
        }

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        doTempCheckOut( project );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        project = addProjectAndCheckOutSources( project, builderType );

        updateProjectFromCheckOut( project );

        return project.getId();
    }

    public void updateProject( String projectId )
        throws ContinuumException
    {
        try
        {
            ContinuumProject project = store.getProject( projectId );

            File workingDirectory = new File( project.getWorkingDirectory() );

            if ( !workingDirectory.exists() )
            {
                getLogger().warn( "Creating missing working directory for project '" + project.getName() + "'." );

                if ( !workingDirectory.exists() )
                {
                    throw new ContinuumException( "Could not make missing working directory for project '" + project.getName() + "'." );
                }
            }

            // ----------------------------------------------------------------------
            // Update the source code
            // ----------------------------------------------------------------------

            try
            {
                scm.updateProject( project );
            }
            catch ( ContinuumScmException e )
            {
                throw new ContinuumException( "Error while updating project.", e );
            }

            updateProjectFromCheckOut( project );
        }
        catch ( ContinuumStoreException ex )
        {
            getLogger().error( "Error while updating project.", ex );

            throw new ContinuumException( "Error while updating project.", ex );
        }
    }

    public void removeProject( String projectId )
        throws ContinuumException
    {
        try
        {
            store.removeProject( projectId );
        }
        catch ( ContinuumStoreException ex )
        {
            getLogger().error( "Error while updating project.", ex );

            throw new ContinuumException( "Error while removing project.", ex );
        }
    }

    public ContinuumProject getProject( String projectId )
        throws ContinuumException
    {
        try
        {
            ContinuumProject project = store.getProject( projectId );

            return project;
        }
        catch ( ContinuumStoreException ex )
        {
            getLogger().error( "Error while finding all projects.", ex );

            throw new ContinuumException( "Exception while getting all projects.", ex );
        }
    }

    public Iterator getAllProjects( int start, int end )
        throws ContinuumException
    {
        try
        {
            Iterator it = store.getAllProjects();

            return it;
        }
        catch ( ContinuumStoreException ex )
        {
            getLogger().error( "Error while finding all projects.", ex );

            throw new ContinuumException( "Exception while getting all projects.", ex );
        }
    }

    public String buildProject( String projectId )
        throws ContinuumException
    {
        try
        {
            ContinuumProject project = store.getProject( projectId );

            ContinuumBuilder builder = builderManager.getBuilder( project.getBuilderId() );

            builder.build( new File( project.getWorkingDirectory() ), project );

            String buildId = store.createBuild( project.getId() );

            getLogger().info( "Enqueuing " + project.getName() + ", projet projectId: " + project.getId() + ", build projectId: " + buildId + "..." );

            buildQueue.enqueue( projectId, buildId );

            return buildId;
        }
        catch ( ContinuumStoreException e )
        {
            getLogger().error( "Error while building project.", e );

            throw new ContinuumException( "Error while creating build object.", e );
        }
        catch ( BuildQueueException e )
        {
            getLogger().error( "Error while enqueuing project.", e );

            throw new ContinuumException( "Error while creating enqueuing object.", e );
        }
    }

    public boolean checkIfProjectNeedsToBeBuilt( String projectId )
        throws ContinuumException
    {
        try
        {
            ContinuumProject project = store.getProject( projectId );

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
            throw new ContinuumException( "Exception while checking the project.", ex );
        }
        catch ( ContinuumStoreException ex )
        {
            throw new ContinuumException( "Exception while checking the project.", ex );
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private ContinuumProject addProjectAndCheckOutSources( ContinuumProject project, String builderType )
        throws ContinuumException
    {
        try
        {
            // ----------------------------------------------------------------------
            // Store the project
            // ----------------------------------------------------------------------

            if ( StringUtils.isEmpty( project.getName() ) )
            {
                throw new ContinuumException( "The name of the project cannot be empty" );
            }

            File projectWorkingDirectory = new File( workingDirectory, project.getName().replace( ' ', '-' ) );

            if ( !projectWorkingDirectory.exists() && !projectWorkingDirectory.mkdirs() )
            {
                throw new ContinuumException( "Could not make the working directory for the project (" + projectWorkingDirectory.getAbsolutePath() + ")." );
            }

            project.setWorkingDirectory( projectWorkingDirectory.getAbsolutePath() );

            scm.checkOutProject( project );

            String projectId = store.addProject( project.getName(),
                                                 project.getScmUrl(),
                                                 project.getNagEmailAddress(),
                                                 project.getVersion(),
                                                 builderType,
                                                 projectWorkingDirectory.getPath(),
                                                 project.getConfiguration() );

            project = store.getProject( projectId );

            return project;
        }
        catch ( ContinuumScmException ex )
        {
            getLogger().error( "Exception while checking out the project.", ex );

            throw new ContinuumException( "Exception while checking out the project.", ex );
        }
        catch ( ContinuumStoreException ex )
        {
            getLogger().error( "Exception while adding project.", ex );

            throw new ContinuumException( "Exception while adding project.", ex );
        }
    }

    private void doTempCheckOut( ContinuumProject project )
        throws ContinuumException
    {
        File checkoutDirectory = new File( workingDirectory, "temp-project" );

        if ( checkoutDirectory.exists() )
        {
            try
            {
                FileUtils.cleanDirectory( checkoutDirectory );
            }
            catch ( IOException ex )
            {
                throw new ContinuumException( "Error while cleaning out " + checkoutDirectory.getAbsolutePath() );
            }
        }
        else
        {
            if ( !checkoutDirectory.mkdirs() )
            {
                throw new ContinuumException( "Could not make the check out directory (" + checkoutDirectory.getAbsolutePath() + ")." );
            }
        }

        // TODO: Get the list of files to check out from the builder.
        // Maven 2: pom.xml, Maven 1: project.xml, Ant: all? build.xml?

        try
        {
            scm.checkOut( project, checkoutDirectory);
        }
        catch ( ContinuumScmException ex )
        {
            throw new ContinuumException( "Error while checking out the project.", ex );
        }
    }

    private void updateProjectFromCheckOut( ContinuumProject project )
        throws ContinuumException
    {
        ContinuumBuilder builder = builderManager.getBuilder( project.getBuilderId() );

        // ----------------------------------------------------------------------
        // Make a new descriptor
        // ----------------------------------------------------------------------

        builder.updateProjectFromCheckOut( new File( project.getWorkingDirectory() ), project );

        // ----------------------------------------------------------------------
        // Store the new descriptor
        // ----------------------------------------------------------------------

        try
        {
            store.updateProject( project.getId(),
                                 project.getName(),
                                 project.getScmUrl(),
                                 project.getNagEmailAddress(),
                                 project.getVersion(),
                                 project.getConfiguration() );
        }
        catch ( ContinuumStoreException e )
        {
            throw new ContinuumException( "Error while storing the updated project.", e );
        }

        getLogger().info( "Updated project: " + project.getName() );
    }

    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    public void initialize()
        throws Exception
    {
        getLogger().info( "Initializing Continuum." );

        PlexusUtils.assertRequirement( builderManager, BuilderManager.ROLE );

        PlexusUtils.assertRequirement( buildQueue, BuildQueue.ROLE );

        PlexusUtils.assertRequirement( store, ContinuumStore.ROLE );

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

        for ( Iterator it = store.getAllProjects(); it.hasNext(); )
        {
            ContinuumProject project = (ContinuumProject) it.next();

            getLogger().info( " " + project.getId() + ":" + project.getName() + ":" + project.getBuilderId() );
        }
    }

    public void start()
        throws Exception
    {
        getLogger().info( "Starting Continuum." );

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

        getLogger().info( "Stopping Continuum." );

        // signal the thread to stop
        builderThread.shutdown();

        builderThreadThread.interrupt();

        while ( !builderThread.isDone() )
        {
            if ( slept > maxSleep )
            {
                getLogger().warn( "Timeout, stopping Continuum." );

                break;
            }

            getLogger().info( "Waiting until Continuum is idling..." );

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
}
