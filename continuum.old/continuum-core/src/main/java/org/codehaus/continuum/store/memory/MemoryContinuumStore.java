package org.codehaus.continuum.store.memory;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;

import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumBuildResult;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ContinuumProjectState;
import org.codehaus.continuum.project.GenericContinuumBuild;
import org.codehaus.continuum.project.GenericContinuumProject;
import org.codehaus.continuum.project.ProjectDescriptor;
import org.codehaus.continuum.store.AbstractContinuumStore;
import org.codehaus.continuum.store.ContinuumStoreException;
import org.codehaus.continuum.store.tx.StoreTransactionManager;
import org.codehaus.continuum.utils.PlexusUtils;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: MemoryContinuumStore.java,v 1.7 2004-10-08 12:15:32 trygvis Exp $
 */
public class MemoryContinuumStore
    extends AbstractContinuumStore
    implements Initializable
{
    /** @requirement */
    private StoreTransactionManager txManager;

    private int projectSerial;

    private int buildSerial;

    private Map projectIdIndex;

    private List projectList;

    private Map buildIdIndex;

    private List buildList;

    /** */
    public MemoryContinuumStore()
    {
    }

    private static class NotNullIterator
        implements Iterator
    {
        private Iterator it;

        private boolean hasNext;

        private Object next;

        public NotNullIterator ( Iterator it )
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

    private static class StartTimeComparator
        implements Comparator
    {
        public int compare(Object o1, Object o2)
        {
            ContinuumBuild b1 = (ContinuumBuild) o1;
            ContinuumBuild b2 = (ContinuumBuild) o2;

            return (int) (b1.getStartTime() - b2.getStartTime());
        }

        public boolean equals( Object other )
        {
            throw new RuntimeException( "Not implemented." );
        }
    }

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws Exception
    {
        PlexusUtils.assertRequirement( txManager, StoreTransactionManager.ROLE );

        createDatabase();
    }

    // ----------------------------------------------------------------------
    // Database methods
    // ----------------------------------------------------------------------

    public void createDatabase()
        throws ContinuumStoreException
    {
        projectSerial = Math.abs( "projectSerial".hashCode() );

        buildSerial = Math.abs( "buildSerial".hashCode() );

        projectIdIndex = new HashMap();

        projectList = new ArrayList();

        buildIdIndex = new HashMap();

        buildList = new ArrayList();
    }

    public void deleteDatabase()
        throws ContinuumStoreException
    {
        // ignored
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

        try
        {
            txManager.enter();

            ContinuumProject project = findProjectByName( name );

            if ( project != null )
            {
                throw new ContinuumStoreException( "The specified project already exists in the store." );
            }

            String projectId = Integer.toString( projectSerial++ );

            project = new GenericContinuumProject( projectId );

            project.setName( name );

            project.setScmConnection( scmConnection );

            project.setState( ContinuumProjectState.NEW );

            project.setType( type );

            projectList.add( project );

            projectIdIndex.put( projectId, new Integer( projectList.size() - 1 ) );

            txManager.leave();

            return projectId;
        }
        catch( ContinuumStoreException ex )
        {
            txManager.rollback();

            throw ex;
        }
    }

    public void removeProject( String projectId )
        throws ContinuumStoreException
    {
        try
        {
            txManager.enter();

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

            txManager.leave();
        }
        catch( ContinuumStoreException ex )
        {
            txManager.rollback();

            throw ex;
        }
    }

    public void setProjectDescriptor( String projectId, ProjectDescriptor descriptor )
        throws ContinuumStoreException
    {
        try
        {
            txManager.enter();

            ContinuumProject project = getProject( projectId );

            ProjectDescriptor originalDescriptor = project.getDescriptor();

            if ( originalDescriptor != null )
            {
                throw new ContinuumStoreException( "The descriptor is already set." );
            }

            project.setDescriptor( descriptor );

            txManager.leave();
        }
        catch( ContinuumStoreException ex )
        {
            txManager.rollback();

            throw ex;
        }
    }

    public void updateProjectDescriptor( String projectId, ProjectDescriptor descriptor )
        throws ContinuumStoreException
    {
        try
        {
            txManager.enter();

            ContinuumProject project = getProject( projectId );

            project.setDescriptor( descriptor );

            txManager.leave();
        }
        catch( ContinuumStoreException ex )
        {
            txManager.rollback();

            throw ex;
        }
    }

    public void updateProject( String projectId, String name, String scmUrl )
        throws ContinuumStoreException
    {
        try
        {
            txManager.enter();

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

            txManager.leave();
        }
        catch( ContinuumStoreException ex )
        {
            txManager.rollback();

            throw ex;
        }
    }

    public Iterator getAllProjects()
        throws ContinuumStoreException
    {
        return new NotNullIterator( projectList.iterator() );
    }

    public Iterator findProjectsByName( String nameSearchPattern )
        throws ContinuumStoreException
    {
        try
        {
            txManager.enter();

            List list = new ArrayList();

            for ( Iterator it = projectList.iterator(); it.hasNext(); )
            {
                ContinuumProject project = (ContinuumProject) it.next();

                if ( project.getName().indexOf( nameSearchPattern ) >= 0 )
                {
                    list.add( project );
                }
            }

            Iterator it = Collections.unmodifiableList( list ).iterator();

            txManager.leave();

            return it;
        }
        catch( ContinuumStoreException ex )
        {
            txManager.rollback();

            throw ex;
        }
    }

    public ContinuumProject getProject( String projectId )
        throws ContinuumStoreException
    {
        try
        {
            txManager.enter();

            if ( projectId == null )
            {
                throw new IllegalArgumentException( "projectId cannot be null." );
            }

            Integer index = (Integer) projectIdIndex.get( projectId );

            if ( index == null )
            {
                throw new ContinuumStoreException( "No such project: " + projectId );
            }

            ContinuumProject project = (ContinuumProject) projectList.get( index.intValue() );

            txManager.leave();

            return project;
        }
        catch( ContinuumStoreException ex )
        {
            txManager.rollback();

            throw ex;
        }
    }

    private ContinuumProject findProjectByName( String name )
        throws ContinuumStoreException
    {
        try
        {
            txManager.enter();

            ContinuumProject found = null;

            for ( Iterator it = projectList.iterator(); it.hasNext(); )
            {
                ContinuumProject project = (ContinuumProject) it.next();

                String candidate = project.getName();

                if ( candidate.equals( name ) )
                {
                    found = project;

                    break;
                }
            }

            txManager.leave();

            return found;
        }
        catch( ContinuumStoreException ex )
        {
            txManager.rollback();

            throw ex;
        }
    }

    // ----------------------------------------------------------------------
    // Build
    // ----------------------------------------------------------------------

    public String createBuild( String projectId )
        throws ContinuumStoreException
    {
        try
        {
            txManager.enter();

            ContinuumProject project = getProject( projectId );

            String buildId = Integer.toString( buildSerial++ );

            GenericContinuumBuild build = new GenericContinuumBuild( buildId );

            build.setProject( project );

            project.setState( ContinuumProjectState.BUILD_SIGNALED );

            build.setState( ContinuumProjectState.BUILD_SIGNALED );

            build.setStartTime( new Date().getTime() );

            buildList.add( build );

            buildIdIndex.put( buildId, new Integer( buildList.size() - 1 ) );

            txManager.leave();

            return buildId;
        }
        catch( ContinuumStoreException ex )
        {
            txManager.rollback();

            throw ex;
        }
    }

    public void setBuildResult( String id, ContinuumProjectState state, ContinuumBuildResult buildResult, Throwable error )
        throws ContinuumStoreException
    {
        try
        {
            txManager.enter();

            GenericContinuumBuild build = getGenericBuild( id );

            GenericContinuumProject project = (GenericContinuumProject)build.getProject();

            project.setState( state );

            build.setState( state );

            build.setEndTime( new Date().getTime() );

            build.setBuildResult( buildResult );

            build.setError( error );

            txManager.leave();
        }
        catch( ContinuumStoreException ex )
        {
            txManager.rollback();

            throw ex;
        }
    }

    public ContinuumBuild getBuild( String id )
        throws ContinuumStoreException
    {
        try
        {
            txManager.enter();

            Integer index = (Integer) buildIdIndex.get( id );

            if ( index == null )
            {
                throw new ContinuumStoreException( "No such build result: " + id );
            }

            txManager.leave();

            return (ContinuumBuild) buildList.get( index.intValue() );
        }
        catch( ContinuumStoreException ex )
        {
            txManager.rollback();

            throw ex;
        }
    }

    public ContinuumBuild getLatestBuildForProject( String projectId )
        throws ContinuumStoreException
    {
        SortedSet builds = getBuildsForProjectAsList( projectId, 0, 0);

        if ( builds.size() == 0 )
        {
            return null;
        }

        return (ContinuumBuild) builds.last();
    }

    public Iterator getBuildsForProject( String projectId, int start, int end )
        throws ContinuumStoreException
    {
        return getBuildsForProjectAsList( projectId, start, end ).iterator();
    }

    // TODO: Implement start and end
    public SortedSet getBuildsForProjectAsList( String projectId, int start, int end )
        throws ContinuumStoreException
    {
        try
        {
            txManager.enter();

            ContinuumProject project = getProject( projectId );

            SortedSet result = new TreeSet( new StartTimeComparator() );

            for ( Iterator it = buildList.iterator(); it.hasNext(); )
            {
                ContinuumBuild buildResult = (ContinuumBuild) it.next();

                if ( buildResult.getProject() == project )
                {
                    result.add( buildResult );
                }
            }

            txManager.leave();

            return result;
        }
        catch( ContinuumStoreException ex )
        {
            txManager.rollback();

            throw ex;
        }
    }
}
