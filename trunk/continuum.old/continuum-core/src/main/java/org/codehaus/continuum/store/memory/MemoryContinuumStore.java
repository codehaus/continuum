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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumBuildResult;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ContinuumProjectState;
import org.codehaus.continuum.project.GenericContinuumBuild;
import org.codehaus.continuum.project.GenericContinuumProject;
import org.codehaus.continuum.project.ProjectDescriptor;
import org.codehaus.continuum.store.AbstractContinuumStore;
import org.codehaus.continuum.store.ContinuumStoreException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: MemoryContinuumStore.java,v 1.2 2004-07-27 05:42:14 trygvis Exp $
 */
public class MemoryContinuumStore
    extends AbstractContinuumStore
{
    private int projectSerial = Math.abs( new Random().nextInt() );

    private int buildResultSerial = Math.abs( new Random().nextInt() );

    private Map projects = new HashMap();

    private Map builds = new HashMap();

    /** */
    public MemoryContinuumStore()
    {
    }

    // ----------------------------------------------------------------------
    // Transaction handling
    // ----------------------------------------------------------------------

    public void beginTransaction()
    {
        // ignored
    }

    public void commitTransaction()
    {
        // ignored
    }

    public void rollbackTransaction()
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

        ContinuumProject project = findProjectByName( name );

        if ( project != null )
            throw new ContinuumStoreException( "The specified project already exists in the store." );

        String id = Integer.toString( projectSerial++ );

        project = new GenericContinuumProject( id );

        project.setName( name );

        project.setScmConnection( scmConnection );

        project.setState( ContinuumProjectState.NEW );

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

    public String createBuild( String projectId )
        throws ContinuumStoreException
    {
        ContinuumProject project = getProject( projectId );

        String id = Integer.toString( buildResultSerial++ );

        GenericContinuumBuild build = new GenericContinuumBuild( id );

        build.setProject( project );

        build.setState( ContinuumProjectState.BUILD_SIGNALED );

        build.setStartTime( new Date().getTime() );

        builds.put( id, build );

        return id;
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
        ContinuumBuild result = (ContinuumBuild) builds.get( id );

        if ( result == null )
        {
            throw new ContinuumStoreException( "No such build result: " + id );
        }

        return result;
    }

    // TODO: Implement start and end
    public Iterator getBuildsForProject( String projectId, int start, int end )
        throws ContinuumStoreException
    {
        ContinuumProject project = getProject( projectId );

        List result = new ArrayList();

        for ( Iterator it = builds.values().iterator(); it.hasNext(); )
        {
            ContinuumBuild buildResult = (ContinuumBuild) it.next();

            if ( buildResult.getProject() == project )
            {
                result.add( buildResult );
            }
        }

        return result.iterator();
    }
}
