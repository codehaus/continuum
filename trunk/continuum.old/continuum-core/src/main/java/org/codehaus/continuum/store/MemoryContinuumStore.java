package org.codehaus.continuum.store;

/*
 * LICENSE
 */

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.codehaus.continuum.project.BuildResult;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.GenericBuildResult;
import org.codehaus.continuum.project.GenericContinuumProject;
import org.codehaus.continuum.project.ProjectDescriptor;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: MemoryContinuumStore.java,v 1.3 2004-07-07 02:34:36 trygvis Exp $
 */
public class MemoryContinuumStore
    extends AbstractContinuumStore
{
    private int projectSerial = Math.abs( new Random().nextInt() );

    private int buildResultSerial = Math.abs( new Random().nextInt() );

    private Map projects = new HashMap();

    private Map buildResults = new HashMap();

    /** */
    public MemoryContinuumStore()
    {
    }

    // ----------------------------------------------------------------------
    // ContinuumProject
    // ----------------------------------------------------------------------

    public String addProject( String name, String scmConnection, String type )
        throws ContinuumStoreException
    {
        ContinuumProject project = findProjectByName( name );

        if ( project != null )
            throw new ContinuumStoreException( "The specified project already exists in the store." );

        String id = Integer.toString( projectSerial++ );

        project = new GenericContinuumProject( id );

        project.setName( name );

        project.setScmConnection( scmConnection );

        project.setState( ContinuumProject.PROJECT_STATE_NEW );

        project.setType( type );

        projects.put( id, project );

        return id;
    }

    public void setProjectDescriptor( String projectId, ProjectDescriptor descriptor )
        throws ContinuumStoreException
    {
        ContinuumProject project = getProject( projectId );

        project.setDescriptor( descriptor );
    }

    public Iterator getAllProjects()
        throws ContinuumStoreException
    {
        return projects.values().iterator();
    }

    public ContinuumProject getProject( String projectId )
        throws ContinuumStoreException
    {
        ContinuumProject project;

        if ( projectId == null )
        {
            throw new IllegalArgumentException( "projectId cannot be null." );
        }

        project = (ContinuumProject) projects.get( projectId );

        if ( project == null )
        {
            throw new ContinuumStoreException( "No such project: " + projectId );
        }

        return project;
    }

    private ContinuumProject findProjectByName( String name)
    {
        for ( Iterator it = projects.values().iterator(); it.hasNext(); )
        {
            ContinuumProject project = (ContinuumProject) it.next();

            String candidate = project.getName();

            if ( candidate.equals( name ) )
                return project;
        }

        return null;
    }

    // ----------------------------------------------------------------------
    // Build
    // ----------------------------------------------------------------------

    public String createBuildResult( String projectId )
        throws ContinuumStoreException
    {
        ContinuumProject project = getProject( projectId );

        String buildId = Integer.toString( buildResultSerial++ );

        GenericBuildResult buildResult = new GenericBuildResult( buildId );

        buildResult.setProject( project );

        buildResult.setState( BuildResult.BUILD_BUILDING );

        buildResult.setStartTime( new Date().getTime() );

        buildResults.put( buildId, buildResult );

        return buildId;
    }

    public void setBuildResult( String buildId, int state, Throwable error )
        throws ContinuumStoreException
    {
        BuildResult buildResult = getBuildResult( buildId );

        buildResult.setState( state );

        buildResult.setEndTime( new Date().getTime() );

        buildResult.setError( error );
    }

    public BuildResult getBuildResult( String buildId )
        throws ContinuumStoreException
    {
        BuildResult result = (BuildResult) buildResults.get( buildId );

        if ( result == null )
        {
            throw new ContinuumStoreException( "No such build result: " + buildId );
        }

        return result;
    }
}
