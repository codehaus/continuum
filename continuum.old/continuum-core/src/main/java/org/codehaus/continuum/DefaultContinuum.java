package org.codehaus.plexus.continuum;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.MavenArtifact;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;

import org.codehaus.plexus.compiler.Compiler;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.continuum.buildqueue.BuildQueue;
import org.codehaus.plexus.continuum.notification.ContinuumNotifier;
import org.codehaus.plexus.continuum.projectstorage.ProjectStorage;
import org.codehaus.plexus.continuum.projectstorage.ProjectStorageException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.util.IOUtil;

public class DefaultContinuum
    extends AbstractLogEnabled
    implements Continuum, Initializable, Startable
{
    // configuration

    private String buildDirectory;

    private String checkoutDirectory;

    // requirements

    private ContinuumBuilder builder;

    private BuildQueue buildQueue;

    private MavenProjectBuilder projectBuilder;

    private Compiler compiler;

    private ContinuumNotifier notifier;

    private ProjectStorage projectStorage;

    // member variables

    private Map builds;

    private boolean addingProject;

    // state

    private boolean building;

    private boolean shutdown;

    ///////////////////////////////////////////////////////////////////////////
    // Component lifecycle

    public void initialize()
        throws Exception
    {
        getLogger().info( "Initializing continuum." );

        builds = new LinkedHashMap();

        assertRequirement( builder, "builder" );
        assertRequirement( buildQueue, "build-queue" );
        assertRequirement( projectBuilder, "project-builder" );
        assertRequirement( compiler, "compiler" );
        assertRequirement( notifier, "notifier" );
        assertRequirement( projectStorage, "project-storage" );

        if( checkoutDirectory == null )
            throw new PlexusConfigurationException( "Missing configuration: checkout directory." );

        if( buildDirectory == null )
            throw new PlexusConfigurationException( "Missing configuration: build directory." );

        File f = new File( checkoutDirectory );

        getLogger().info( "Using " + checkoutDirectory + " as checkout directory." );
        if ( !f.exists() )
        {
            getLogger().info( "Checkout directory does not exist, creating." );
            f.mkdirs();
        }

        getLogger().info( "Continuum initialized." );
    }

    public void start()
        throws Exception
    {
        getLogger().info( "Starting continuum." );

        // start the builder thread
        Thread thread = new Thread( new BuilderThread() );
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
        shutdown = true;

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

        getLogger().info( "Continuum stopped." );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Continuum implementation

    public void addProject( String projectUrl )
        throws ContinuumException
    {
        // We will simply deal with POMs that can be retrieved from
        // the local file system or over http.

        addingProject = true;

        MavenProject project = null;

        try
        {
            if ( projectUrl.startsWith( "http://" ) )
            {
                try
                {
                    getLogger().info( "Downloading from " + projectUrl );
    
                    URL url = new URL( projectUrl );
    
                    InputStream is = url.openStream();
    
                    // for now
    
                    String contents = IOUtil.toString( is );
    
                    File file = File.createTempFile( "continuum-project-", ".xml" );
                    FileWriter writer = new FileWriter( file );
    
                    IOUtil.copy( contents, writer );
    
                    writer.close();
    
                    project = projectBuilder.build( file );
    
                    file.delete();
    
                    projectStorage.storeProject( project.getGroupId(), project.getArtifactId(), new StringReader( contents ) );
                }
                catch ( ProjectStorageException ex )
                {
                    getLogger().fatalError( "Error while storing the POM.", ex );

                    throw new ContinuumException( "Error while storing the POM.", ex );
                }
                catch ( ProjectBuildingException ex )
                {
                    getLogger().fatalError( "Can't build the POM read from url: " + projectUrl, ex );
    
                    throw new ContinuumException( "Can't build the POM read from url: " + projectUrl, ex );
                }
                catch ( IOException ex )
                {
                    getLogger().fatalError( "Can't read POM from url: " + projectUrl, ex );
    
                    throw new ContinuumException( "Could not add project: " + projectUrl, ex );
                }
            }
            else
            {
                try
                {
                    File file = new File( projectUrl );

                    project = projectBuilder.build( file );
    
                    projectStorage.storeProject( project.getGroupId(), project.getArtifactId(), new FileReader( file ) );
                }
                catch ( Exception ex )
                {
                    getLogger().error( "Can't read POM from file system: " + projectUrl, ex );
    
                    throw new ContinuumException( "Could not add project: " + projectUrl, ex );
                }
            }

            // If we successfully created a project we're ready to go!
            if ( project != null )
            {
                addProject( project );
            }
        }
        finally
        {
            addingProject = false;
        }
    }

    public String buildProject( String groupId, String artifactId )
        throws ContinuumException
    {
        MavenProject project = getProject( groupId, artifactId );

        getLogger().info( "Enqueuing " + createId( groupId, artifactId ) + " ..." );

        return buildQueue.enqueue( project );
    }

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

    public int getState()
    {
        if ( building || addingProject )
            return ContinuumConstants.WORKING;

        return ContinuumConstants.IDLE;
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

    ///////////////////////////////////////////////////////////////////////////
    // Private

    private class BuilderThread
        implements Runnable
    {
        public void run()
        {
            while ( !shutdown )
            {
                MavenProject project = getProject();

                if( project != null )
                {
                    builder.build( project );
//                    buildProject( project );
                }
                else
                {
                    getLogger().info( "Builder sleeping..." );
                    sleep( 1000 );
                }
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

    private void addProject( MavenProject project )
        throws ContinuumException
    {
        MavenProjectBuild build = null;

        addingProject = true;

        try
        {
            build = new MavenProjectBuild( project );

            String id = createId( project.getGroupId(), project.getArtifactId() );

            builds.put( id, build );

            getLogger().info( "Adding project: " + project.getName() );
        }
        catch ( Exception ex )
        {
            getLogger().error( "Cannot add project!", ex );

            throw new ContinuumException( "Exception while building project.", ex );
        }
        finally
        {
            addingProject = false;
        }
    }

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

    private String[] classpathElements( MavenProject project )
    {
        String[] classpathElements = new String[project.getArtifacts().size()];

        for ( int i = 0; i < classpathElements.length; i++ )
        {
            classpathElements[i] = ( (MavenArtifact) project.getArtifacts().get( i ) ).getPath();
        }

        return classpathElements;
    }

    private boolean hasProject( String groupId, String artifactId )
    {
        return builds.containsKey( createId( groupId, artifactId ) );
    }

    private MavenProject getProject( String groupId, String artifactId )
        throws ContinuumException
    {
        String id = createId( groupId, artifactId );

        MavenProject project = (MavenProject)builds.get( id );

        if ( project == null )
            throw new ContinuumException( "No such project: " + id + "." );

        return project;
    }

    private String createId( String groupId, String artifactId )
    {
        return groupId + ":" + artifactId;
    }

    private void assertRequirement( Object requirement, String name )
        throws PlexusConfigurationException
    {
        if ( requirement == null )
            throw new PlexusConfigurationException( "Missing requirement '" + name + "'." );
    }
}
