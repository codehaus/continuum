package org.codehaus.continuum.store.stash;

import java.io.File;
import java.util.Iterator;
import java.util.Properties;

import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumBuildResult;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ContinuumProjectState;
import org.codehaus.continuum.project.DefaultContinuumProject;
import org.codehaus.continuum.store.AbstractContinuumStore;
import org.codehaus.continuum.store.ContinuumStoreException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.stash.obie.ObjectDatabase;
import org.codehaus.stash.obie.ObjectStore;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: StashContinuumStore.java,v 1.1.1.1 2005-02-17 22:23:53 trygvis Exp $
 */
public class StashContinuumStore
    extends AbstractContinuumStore
    implements Initializable, Startable
{
    private String databaseDirectory;

    private ObjectDatabase database;

    private ObjectStore projectStore;

    private boolean databaseCreated;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws Exception
    {
        if ( !databaseCreated )
        {
            createDatabase();

            databaseCreated = true;
        }
    }

    public void start()
        throws ContinuumStoreException
    {
    }

    public void stop()
        throws ContinuumStoreException
    {
    }

    // ----------------------------------------------------------------------
    // Database methods
    // ----------------------------------------------------------------------

    public void createDatabase()
        throws ContinuumStoreException
    {
        File f = new File( databaseDirectory );

        if ( !f.exists() )
        {
            f.mkdirs();
        }

        try
        {
            database = new ObjectDatabase( databaseDirectory );

            projectStore = database.createStore( ContinuumProject.class );

            projectStore.createIndex( String.class, "name" );
        }
        catch ( Exception e )
        {
            throw new ContinuumStoreException( "Cannot create database.", e );
        }
    }

    public void deleteDatabase()
        throws ContinuumStoreException
    {
    }

    // ----------------------------------------------------------------------
    // ContinuumProject
    // ----------------------------------------------------------------------

    public String addProject( String name,
                              String scmConnection,
                              String nagEmailAddress,
                              String version,
                              String builderType,
                              String workingDirectory,
                              Properties configuration )
        throws ContinuumStoreException
    {
        ContinuumProject project = new DefaultContinuumProject();

        project.setName( name );

        project.setScmUrl( scmConnection );

        project.setNagEmailAddress( nagEmailAddress );

        project.setVersion( version );

        project.setState( ContinuumProjectState.NEW );

        project.setBuilderId( builderType );

        project.setWorkingDirectory( workingDirectory );

        project.setConfiguration( configuration );

        System.out.println( project );

        try
        {
            String id = Long.toString( projectStore.insert( project ) );

            //!!!
            // This is the only way I can get the id into the object, Not sure what the best
            // thing to do here is. Not sure how to do it intrinsically.

            project.setId( id );

            projectStore.update( id, project );

            System.out.println( project );

            return id;
        }
        catch ( Exception e )
        {
            throw new ContinuumStoreException( "Cannot insert new ContinuumProject.", e );
        }
    }

    public void removeProject( String projectId )
        throws ContinuumStoreException
    {
        try
        {
            projectStore.delete( projectId );
        }
        catch ( Exception e )
        {
            throw new ContinuumStoreException( "Cannot delete ContinuumProject.", e );
        }
    }

    public void updateProject( String projectId,
                               String name,
                               String scmUrl,
                               String nagEmailAddress,
                               String version,
                               Properties configuration )
        throws ContinuumStoreException
    {
        try
        {
            ContinuumProject project = (ContinuumProject) projectStore.fetch( projectId );

            project.setName( name );

            project.setScmUrl( scmUrl );

            project.setNagEmailAddress( nagEmailAddress );

            project.setVersion( version );

            project.setConfiguration( configuration );

            projectStore.update( projectId, project );
        }
        catch ( Exception e )
        {
            throw new ContinuumStoreException( "Cannot delete ContinuumProject.", e );
        }
    }

    public Iterator getAllProjects()
        throws ContinuumStoreException
    {
        try
        {
            return projectStore.fetchAll( "name" );
        }
        catch ( Exception e )
        {
            throw new ContinuumStoreException( "Cannot fetch all projects.", e );
        }
    }

    public Iterator findProjectsByName( String nameSearchPattern )
        throws ContinuumStoreException
    {
        return null;
    }

    public ContinuumProject getProject( String projectId )
        throws ContinuumStoreException
    {
        System.out.println( "projectId = " + projectId );

        try
        {
            return (ContinuumProject) projectStore.fetch( projectId );
        }
        catch ( Exception e )
        {
            throw new ContinuumStoreException( "Cannot retrieve ContinuumProject.", e );
        }
    }

    // ----------------------------------------------------------------------
    // Build
    // ----------------------------------------------------------------------

    public String createBuild( String projectId )
        throws ContinuumStoreException
    {
        return null;
    }

    public void setBuildResult( String buildId, ContinuumProjectState state, ContinuumBuildResult result, Throwable error )
        throws ContinuumStoreException
    {
    }

    public ContinuumBuild getBuild( String buildId )
        throws ContinuumStoreException
    {
        return null;
    }


    public ContinuumBuild getLatestBuildForProject( String projectId )
        throws ContinuumStoreException
    {
        return null;
    }

    public Iterator getBuildsForProject( String projectId, int start, int end )
        throws ContinuumStoreException
    {
        return null;
    }
}
