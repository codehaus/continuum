package org.codehaus.continuum.store;

/*
 * Copyright 2004-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumBuildResult;
import org.codehaus.continuum.project.ContinuumPrevaylerStore;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ContinuumProjectState;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ModelloPrevaylerContinuumStore.java,v 1.2 2005-03-10 00:05:53 trygvis Exp $
 */
public class ModelloPrevaylerContinuumStore
    extends AbstractContinuumStore
    implements ContinuumStore, Initializable
{
    private ContinuumPrevaylerStore store;

    private int nextProjectId;

    private int nextBuildId;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
    {
    }

    // ----------------------------------------------------------------------
    // ContinuumStore Implementation
    // ----------------------------------------------------------------------

    // ----------------------------------------------------------------------
    // Database methods
    // ----------------------------------------------------------------------

    public void createDatabase()
        throws ContinuumStoreException
    {
    }

    public void deleteDatabase()
        throws ContinuumStoreException
    {
    }

    // ----------------------------------------------------------------------
    // ContinuumProject
    // ----------------------------------------------------------------------

    public String addProject( String name, String scmUrl, String nagEmailAddress, String version, String builderId,
                              String workingDirectory, Properties properties )
        throws ContinuumStoreException
    {
        ContinuumProject project;

        project = new ContinuumProject();

        project.setId( Integer.toString( nextProjectId++ ) );

        project.setName( name );
        project.setScmUrl( scmUrl );
        project.setNagEmailAddress( nagEmailAddress );
        project.setVersion( version );
        project.setBuilderId( builderId );
        project.setWorkingDirectory( workingDirectory );

        try
        {
            store.addContinuumProject( project.getId(), project );
        }
        catch ( Exception e )
        {
            throw new ContinuumStoreException( "Error while adding a project.", e );
        }

        return project.getId();
    }

    public void removeProject( String projectId )
        throws ContinuumStoreException
    {
        try
        {
            store.deleteContinuumProject( projectId );
        }
        catch ( Exception e )
        {
            throw new ContinuumStoreException( "Error while adding a project.", e );
        }
    }

    public void updateProject( String projectId, String name, String scmUrl, String nagEmailAddress, String version,
                               Properties properties )
        throws ContinuumStoreException
    {
        try
        {
            ContinuumProject project = store.getContinuumProject( projectId );

            project.setName( name );
            project.setScmUrl( scmUrl );
            project.setNagEmailAddress( nagEmailAddress );
            project.setVersion( version );

            store.updateContinuumProject( projectId, project );
        }
        catch ( Exception e )
        {
            throw new ContinuumStoreException( "Error while updating project.", e );
        }
    }

    public Iterator getAllProjects()
        throws ContinuumStoreException
    {
        try
        {
            Iterator it = store.getContinuumProjectCollection().iterator();

            List projects = new ArrayList();

            while ( it.hasNext() )
            {
                ContinuumProject continuumProject = (ContinuumProject) it.next();

                projects.add( continuumProject );
            }

            return projects.iterator();
        }
        catch ( Exception e )
        {
            throw new ContinuumStoreException( "Error while loading project set", e );
        }
    }

    public Iterator findProjectsByName( String nameSearchPattern )
        throws ContinuumStoreException
    {
        Iterator it = getAllProjects();

        List hits = new ArrayList();

        while ( it.hasNext() )
        {
            ContinuumProject continuumProject = (ContinuumProject) it.next();

            if ( continuumProject.getName().toLowerCase().indexOf( nameSearchPattern.toLowerCase() ) != -1 )
            {
                hits.add( continuumProject );
            }
        }

        return hits.iterator();
    }

    public ContinuumProject getProject( String projectId )
        throws ContinuumStoreException
    {
        try
        {
            ContinuumProject project = store.getContinuumProject( projectId );

            return project;
        }
        catch ( Exception e )
        {
            throw new ContinuumStoreException( "Error while loading project.", e );
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
            ContinuumProject project = getProject( projectId );

            project.setState( ContinuumProjectState.BUILD_SIGNALED );

            ContinuumBuild build = new ContinuumBuild();

            build.setProject( project );

            build.setId( Integer.toString( nextBuildId++ ) );

            build.setState( ContinuumProjectState.BUILD_SIGNALED );

            build.setStartTime( new Date().getTime() );

            store.addContinuumBuild( build.getId(), build );

            return build.getId();
        }
        catch ( Exception e )
        {
            throw new ContinuumStoreException( "Error while creating continuum build for project: '" + projectId + "'.", e );
        }
    }

    public void setBuildResult( String buildId, int state, ContinuumBuildResult result,
                                Throwable error )
        throws ContinuumStoreException
    {
        try
        {
            ContinuumBuild build = getBuild( buildId );

            ContinuumProject project = build.getProject();

            project.setState( state );

            build.setState( state );

            build.setEndTime( new Date().getTime() );

            build.setBuildResult( result );

            build.setError( throwableToString( error ) );
        }
        catch ( Exception e )
        {
            throw new ContinuumStoreException( "Error while setting build result for build: '" + buildId + "'.", e );
        }
    }

    public ContinuumBuild getBuild( String buildId )
        throws ContinuumStoreException
    {
        try
        {
            return store.getContinuumBuild( buildId );
        }
        catch ( Exception e )
        {
            throw new ContinuumStoreException( "Error while loading build id: '" + buildId + "'.", e );
        }
    }

    public ContinuumBuild getLatestBuildForProject( String projectId )
        throws ContinuumStoreException
    {
        try
        {
            List builds = store.getContinuumProject( projectId ).getBuilds();

            if ( builds.size() == 0 )
            {
                return null;
            }

            return (ContinuumBuild) builds.get( builds.size() - 1 );
        }
        catch ( Exception e )
        {
            throw new ContinuumStoreException( "Error while loading last build for project id: '" + projectId + "'.", e );
        }
    }

    public Iterator getBuildsForProject( String projectId, int start, int end )
        throws ContinuumStoreException
    {
        try
        {
            return store.getContinuumProject( projectId ).getBuilds().iterator();
        }
        catch ( Exception e )
        {
            throw new ContinuumStoreException( "Error while builds for project id: '" + projectId + "'.", e );
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------
}
