package org.codehaus.plexus.continuum;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.maven.artifact.MavenArtifact;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;

import org.codehaus.plexus.compiler.Compiler;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.continuum.notification.ContinuumNotifier;
import org.codehaus.plexus.continuum.projectstorage.ProjectStorage;
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

    private MavenProjectBuilder projectBuilder;

    private Compiler compiler;

    private ContinuumNotifier notifier;

    private ProjectStorage projectStorage;

    // member variables
    private Map builds;

    private boolean building;

    private boolean addingProject;

    ///////////////////////////////////////////////////////////////////////////
    // Component lifecycle

    public void initialize()
        throws Exception
    {
        getLogger().info( "Initializing continuum." );

        builds = new LinkedHashMap();

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
    }

    public void stop()
        throws Exception
    {
        int maxSleep = 10 * 1000; // 10 seconds
        int interval = 1000;
        int slept = 0;

        getLogger().info( "Stopping continuum." );

        while( isWorking() )
        {
            if ( slept > maxSleep )
                getLogger().warn( "Timeout, stopping continuum." );

            getLogger().info( "Waiting untill continuum is idling..." );
            Thread.sleep( interval );

            slept += interval;
        }

        getLogger().info( "Continuum stopped." );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Continuum implementation

    public void addProject( String projectUrl )
    {
        // We will simply deal with POMs that can be retrieved from
        // the local file system or over http.

        addingProject = true;

        MavenProject project = null;

        if ( projectUrl.startsWith( "http://" ) )
        {
            try
            {
                getLogger().info( "Downloading project.xml" );

                URL url = new URL( projectUrl );

                InputStream is = url.openStream();

                // for now

                String contents = IOUtil.toString( is );

                File file = File.createTempFile( "continuum-project-", ".xml" );
                FileWriter writer = new FileWriter( file );

                getLogger().info("filename:" + file.getPath());

                IOUtil.copy( contents, writer );

                writer.close();

                project = projectBuilder.build( file );

                file.delete();

                projectStorage.storeProject( project.getGroupId(), project.getArtifactId(), new StringReader( contents ) );
            }
            catch ( Exception e )
            {
                getLogger().error( "Can't read POM from url: " + projectUrl, e );
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
            catch ( Exception e )
            {
                getLogger().error( "Can't read POM from file system: " + projectUrl, e );
            }
        }

        // If we successfully created a project we're ready to go!
        if ( project != null )
        {
            addProject( project );
        }

        addingProject = false;
    }

    public void addProject( MavenProject project )
    {
        MavenProjectBuild build = null;

        addingProject = true;

        try
        {
            build = new MavenProjectBuild( project );

            getLogger().info( "Adding project: " + project.getName() );
        }
        catch ( Exception e )
        {
            getLogger().error( "Cannot add project!", e );
        }

        builds.put( project.getGroupId() + ":" + project.getArtifactId(), build );

        addingProject = false;
    }

    public void buildProject( String groupId, String artifactId )
        throws Exception
    {
        buildProject( (MavenProjectBuild) builds.get( groupId + ":" + artifactId ) );
    }

    public void buildProjects()
    {
        getLogger().info( "Building Projects ..." );

        building = true;

        for ( Iterator i = builds.values().iterator(); i.hasNext(); )
        {
            MavenProjectBuild projectBuild = (MavenProjectBuild) i.next();

            try
            {
                List messages = buildProject( projectBuild );

                StringBuffer message = new StringBuffer();

                // Notification is there are failures.
                if ( messages.size() > 0 )
                {
                    for ( Iterator j = messages.iterator(); j.hasNext(); )
                    {
                        message.append( j.next() ).append( "\n" );
                    }

                    getLogger().info( "Notifying!" );
                }
                else
                {
                    message.append( "Build OK." );
                }

                notifier.notifyAudience( projectBuild.getProject(), message.toString() );
            }
            catch ( Exception e )
            {
                e.printStackTrace();

                StringWriter writer = new StringWriter();

                PrintWriter w = new PrintWriter( writer );

                e.printStackTrace( w );

                try
                {
                    notifier.notifyAudience( projectBuild.getProject(), writer.toString() );
                }
                catch ( Exception ex )
                {
                    getLogger().fatalError( "Could not send notify", ex);
                }
            }
        }

        building = false;
    }

    public boolean isWorking()
    {
        return building || addingProject;
    }

    private List buildProject( MavenProjectBuild build )
        throws Exception
    {
        // We need to check out the sources
        build.getProjectScm().checkout( checkoutDirectory );

        getLogger().info( "Done checking out the project!" );

        String destinationDirectory = buildDirectory + "/target/classes";
//        build.getProject().setProperty( "basedir", buildDirectory );

        System.err.println( new TreeMap( build.getProject().getProperties() ).toString().replace( ',', '\n' ) );

        List messages = compiler.compile( classpathElements( build.getProject() ),
                                          new String[]{build.getProject().getBuild().getSourceDirectory()},
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
}
