package org.codehaus.continuum.projectstorage;

/*
 * LICENSE
 */

import java.io.File;
import java.io.Reader;
import java.util.Iterator;

import org.apache.maven.project.MavenProjectBuilder;

import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: FileContinuumStore.java,v 1.4 2004-06-27 22:20:27 trygvis Exp $
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

    }

    ///////////////////////////////////////////////////////////////////////////
    // ProjectStore implementation

    public String storeProject( ContinuumProject project )
        throws ProjectStorageException
    {
/*
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
*/
        return null;
    }

    public Iterator getAllProjects()
        throws ProjectStorageException
    {
/*
        List projects = new ArrayList();

        try
        {
            Iterator groups = FileUtils.getFiles( storage, "**", null ).iterator();
    
            while ( groups.hasNext() )
            {
                File group = (File)groups.next();
    
                if ( !group.isFile() )
                    continue;
    
                File pom = FileUtils.resolveFile( storage, group.getPath() );

                projects.add( projectBuilder.build( pom ) );
            }
        }
        catch( IOException ex )
        {
            throw new ProjectStorageException( "Exception while reading from project storage.", ex );
        }
        catch( ProjectBuildingException ex )
        {
            throw new ProjectStorageException( "Exception while building the project description.", ex );
        }

        return projects.iterator();
*/
        return null;
    }

    public ContinuumProject getProject( String id )
        throws ProjectStorageException
    {
/*
        try
        {
            return projectBuilder.build( new File( storage, groupId + File.pathSeparator + artifactId ) );
        }
        catch( ProjectBuildingException ex )
        {
            throw new ProjectStorageException( "The projectdatabase is corrupted, could not read the project.", ex );
        }
*/
        return null;
    }
}
