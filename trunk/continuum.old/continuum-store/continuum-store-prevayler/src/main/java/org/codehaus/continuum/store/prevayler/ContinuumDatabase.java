package org.codehaus.continuum.store.prevayler;

/*
 * LICENSE
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumBuildResult;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ContinuumProjectState;
import org.codehaus.continuum.project.GenericContinuumBuild;
import org.codehaus.continuum.project.GenericContinuumProject;
import org.codehaus.continuum.project.ProjectDescriptor;
import org.codehaus.continuum.store.ContinuumStoreException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumDatabase.java,v 1.2 2004-10-08 09:11:32 trygvis Exp $
 */
public class ContinuumDatabase
    implements Serializable
{
    private int projectSerial = Math.abs( "projectSerial".hashCode() );

    private int buildSerial = Math.abs( "buildSerial".hashCode() );

    private final Map projectIdIndex = new HashMap();

    private final List projectList = new ArrayList();

    private final Map buildIdIndex = new HashMap();

    private final List buildList = new ArrayList();

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private static class NotNullIterator
        implements Iterator
    {
        private Iterator it;

        private boolean hasNext;

        private Object next;

        public NotNullIterator( Iterator it )
        {
            this.it = it;
        }

        public void remove()
        {
            it.remove();
        }

        public Object next()
        {
            if ( !hasNext )
            {
                if ( !setNext() )
                {
                    throw new NoSuchElementException();
                }
            }

            hasNext = false;

            return next;
        }

        public boolean hasNext()
        {
            if ( hasNext )
            {
                return true;
            }
            else
            {
                return setNext();
            }
        }

        private boolean setNext()
        {
            while ( it.hasNext() )
            {
                Object object = it.next();

                if ( object != null )
                {
                    next = object;

                    hasNext = true;

                    return true;
                }
            }

            return false;
        }
    }

    // ----------------------------------------------------------------------
    // ContinuumProject
    // ----------------------------------------------------------------------

    public String addProject( String name, String scmConnection, String type )
        throws ContinuumStoreException
    {
        if ( name == null )
        {
            throw new ContinuumStoreException( "name cannot be null." );
        }

        if ( scmConnection == null )
        {
            throw new ContinuumStoreException( "scmConnection cannot be null." );
        }

        if ( type == null )
        {
            throw new ContinuumStoreException( "type cannot be null." );
        }

        ContinuumProject project = findProjectByName( name );

        if ( project != null )
        {
            throw new ContinuumStoreException( "The specified project already exists in the store. Project name: '" + name + "'. Project id: " + project.getId() );
        }

        String projectId = Integer.toString( projectSerial++ );

        project = new GenericContinuumProject( projectId );

        project.setName( name );

        project.setScmConnection( scmConnection );

        project.setState( ContinuumProjectState.NEW );

        project.setType( type );

        projectList.add( project );

        projectIdIndex.put( projectId, new Integer( projectList.size() - 1 ) );

        return projectId;
    }

    public void removeProject( String projectId )
        throws ContinuumStoreException
    {
        Integer index = (Integer)projectIdIndex.remove( projectId );

        if ( index == null )
        {
            throw new ContinuumStoreException( "No such project (" + projectId + ")." );
        }

        projectList.set( index.intValue(), null );

        for( Iterator it = buildIdIndex.values().iterator(); it.hasNext(); )
        {
            index = (Integer) it.next();

            ContinuumBuild build = (ContinuumBuild) buildList.get( index.intValue() );

            if ( projectId.equals( build.getProject().getId() ) )
            {
                it.remove();

                buildIdIndex.remove( build.getId() );
            }
        }
    }

    public void setProjectDescriptor( String projectId, ProjectDescriptor descriptor )
        throws ContinuumStoreException
    {
        ContinuumProject project = getProject( projectId );

        ProjectDescriptor originalDescriptor = project.getDescriptor();

        if ( originalDescriptor != null )
        {
            throw new ContinuumStoreException( "The descriptor is already set." );
        }

        project.setDescriptor( descriptor );
    }

    public void updateProjectDescriptor( String projectId, ProjectDescriptor descriptor )
        throws ContinuumStoreException
    {
        ContinuumProject project = getProject( projectId );

        project.setDescriptor( descriptor );
    }

    public void updateProject( String projectId, String name, String scmUrl )
        throws ContinuumStoreException
    {
        GenericContinuumProject project = getGenericProject( projectId );

        if ( name == null || name.trim().length() == 0 )
        {
            throw new ContinuumStoreException( "The name must be set." );
        }

        if ( scmUrl == null || scmUrl.trim().length() == 0 )
        {
            throw new ContinuumStoreException( "The scm url must be set." );
        }

        project.setName( name );

        project.setScmConnection( scmUrl );
    }

    public Iterator getAllProjects()
        throws ContinuumStoreException
    {
        return new NotNullIterator( projectList.iterator() );
    }

    public Iterator findProjectsByName( String nameSearchPattern )
        throws ContinuumStoreException
    {
        List list = new ArrayList();

        for ( Iterator it = getAllProjects(); it.hasNext(); )
        {
            ContinuumProject project = (ContinuumProject) it.next();

            if ( project.getName().indexOf( nameSearchPattern ) >= 0 )
            {
                list.add( project );
            }
        }

        Iterator it = Collections.unmodifiableList( list ).iterator();

        return it;
    }

    public ContinuumProject getProject( String projectId )
        throws ContinuumStoreException
    {
        if ( projectId == null )
        {
            throw new IllegalArgumentException( "projectId cannot be null." );
        }

        Integer index = (Integer) projectIdIndex.get( projectId );

        if ( index == null )
        {
            throw new ContinuumStoreException( "No such project: " + projectId );
        }

        return (ContinuumProject) projectList.get( index.intValue() );
    }

    private ContinuumProject findProjectByName( String name )
        throws ContinuumStoreException
    {
        ContinuumProject project = null;

        for ( Iterator it = getAllProjects(); it.hasNext(); )
        {
            project = (ContinuumProject) it.next();

            String candidate = project.getName();

            if ( candidate.equalsIgnoreCase( name ) )
            {
                return project;
            }
        }

        return null;
    }

    // ----------------------------------------------------------------------
    // Build
    // ----------------------------------------------------------------------

    public String createBuild( String projectId )
        throws ContinuumStoreException
    {
        ContinuumProject project = getProject( projectId );

        String buildId = Integer.toString( buildSerial++ );

        GenericContinuumBuild build = new GenericContinuumBuild( buildId );

        build.setProject( project );

        project.setState( ContinuumProjectState.BUILD_SIGNALED );

        build.setState( ContinuumProjectState.BUILD_SIGNALED );

        build.setStartTime( new Date().getTime() );

        buildList.add( build );

        buildIdIndex.put( buildId, new Integer( buildList.size() - 1 ) );

        return buildId;
    }

    public void setBuildResult( String id, ContinuumProjectState state, ContinuumBuildResult buildResult, Throwable error )
        throws ContinuumStoreException
    {
        GenericContinuumBuild build = getGenericBuild( id );

        GenericContinuumProject project = (GenericContinuumProject)build.getProject();

        project.setState( state );

        build.setState( state );

        build.setEndTime( new Date().getTime() );

        build.setBuildResult( buildResult );

        build.setError( error );
    }

    public ContinuumBuild getBuild( String id )
        throws ContinuumStoreException
    {
        Integer index = (Integer) buildIdIndex.get( id );

        if ( index == null )
        {
            throw new ContinuumStoreException( "No such build result: " + id );
        }

        return (ContinuumBuild) buildList.get( index.intValue() );
    }

    public ContinuumBuild getLatestBuildForProject( String projectId )
        throws ContinuumStoreException
    {
        List builds = getBuildsForProject( projectId, 0, 0);

        Iterator it = builds.iterator();

        if ( !it.hasNext() )
        {
            return null;
        }

        ContinuumBuild max = (ContinuumBuild) it.next();

        while( it.hasNext() )
        {   
            ContinuumBuild current = (ContinuumBuild) it.next();

            if ( current.getStartTime() > max.getStartTime() )
            {
                max = current;
            }
        }

        return max;
    }

    // TODO: Implement start and end
    public List getBuildsForProject( String projectId, int start, int end )
        throws ContinuumStoreException
    {
        ContinuumProject project = getProject( projectId );

        List result = new ArrayList();

        for ( Iterator it = buildList.iterator(); it.hasNext(); )
        {
            ContinuumBuild buildResult = (ContinuumBuild) it.next();

            if ( buildResult.getProject() == project )
            {
                result.add( buildResult );
            }
        }

        return result;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void dump()
        throws ContinuumStoreException
    {
        System.err.println( "Dumping continuum database:" );

        int i;

        Iterator it;

        System.err.println( "Projects:" );

        for ( i = 0, it = getAllProjects(); it.hasNext(); i++ )
        {
            ContinuumProject project = (ContinuumProject) it.next();

            if ( project == null )
            {
                continue;
            }

            System.err.println( "#" + i + ":" + project.getId() + ", name='" + project.getName() + "'" );
        }

        System.err.println();

        System.err.println( "Builds:" );

        for ( i = 0, it = buildList.iterator(); it.hasNext(); i++ )
        {
            ContinuumBuild build = (ContinuumBuild) it.next();

            if ( build == null )
            {
                continue;
            }

            System.err.println( "#" + i + ":" + build.getId() + ", state: " + build.getState() );
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    protected GenericContinuumProject getGenericProject( String id )
        throws ContinuumStoreException
    {
        return (GenericContinuumProject) getProject( id );
    }

    protected GenericContinuumBuild getGenericBuild( String id )
        throws ContinuumStoreException
    {
        return (GenericContinuumBuild) getBuild( id );
    }
}
