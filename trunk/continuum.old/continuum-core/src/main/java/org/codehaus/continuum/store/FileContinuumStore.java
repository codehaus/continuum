package org.codehaus.plexus.continuum.projectstorage;

/*
 * LISENCE
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;

import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: FileContinuumStore.java,v 1.1 2004-04-07 15:56:56 trygvis Exp $
 */
public class FileProjectStorage
    extends AbstractLogEnabled
    implements ProjectStorage, Initializable
{
    ///////////////////////////////////////////////////////////////////////////
    // Configuration

    private String storageDirectory;

    private MavenProjectBuilder projectBuilder;

    // member variables
    private Map projects;

    private File storage;

    ///////////////////////////////////////////////////////////////////////////
    // Lifecycle

    public void initialize()
        throws Exception
    {
        if ( storageDirectory == null )
            throw new PlexusConfigurationException( "Missing configuration element: storage directory." );

        getLogger().info( "Using " + storageDirectory + " as storage directory." );

        storage = new File( storageDirectory );

        if ( !storage.exists() )
        {
            getLogger().info( "Storage directory did not exist, creating.");
            storage.mkdirs();
        }

        // Now lets bring up all the projects that we have stored, if there are any.

        projects = new HashMap();

        Iterator groups = FileUtils.getFiles( storage, "**", null ).iterator();

        while ( groups.hasNext() )
        {
            File group = (File)groups.next();

            if ( !group.isFile() )
                continue;

            File pom = FileUtils.resolveFile( storage, group.getPath() );
            String name = pom.getPath();
            name = name.substring( FileUtils.dirname( FileUtils.dirname( name ) ) .length() + 1);

            MavenProject project = projectBuilder.build( pom );

            try
            {
                projects.put( project.getGroupId() + "-" + project.getArtifactId(), project );
                getLogger().info( "Added " + project.getGroupId() + ":" + project.getArtifactId() );
            }
            catch ( Exception e )
            {
                getLogger().error( "Cannot process pom: " + pom, e );
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // ProjectStore implementation

    public void storeProject( String groupId, String artifactId, Reader input )
        throws ProjectStorageException
    {
        File file;

        file = new File( storage, groupId + "/" + artifactId + ".pom" );

        FileUtils.mkdir( FileUtils.dirname( file.getPath() ) );

        try
        {
            IOUtil.copy( input, new FileWriter( file ) );
        }
        catch( IOException ex )
        {
            throw new ProjectStorageException( "Exception while persisting project.", ex );
        }
    }

    public Iterator getAllProjects()
        throws ProjectStorageException
    {
        return projects.values().iterator();
    }

    public MavenProject getProject( String groupId, String artifactId )
        throws ProjectStorageException
    {
        return (MavenProject)projects.get( groupId + "-" + artifactId );
    }
}
