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
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.jdo.JDOHelper;

import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumBuildResult;
import org.codehaus.continuum.project.ContinuumJPoxStore;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ContinuumProjectState;
import org.codehaus.plexus.jdo.JdoFactory;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ModelloJPoxContinuumStore.java,v 1.4 2005-03-10 00:05:53 trygvis Exp $
 */
public class ModelloJPoxContinuumStore
    extends AbstractContinuumStore
    implements ContinuumStore, Initializable
{
    /** @requirement */
    private JdoFactory jdoFactory;

    private ContinuumJPoxStore store;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
    {
        store = new ContinuumJPoxStore( jdoFactory.getPersistenceManagerFactory() );
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
                              String workingDirectory, Properties configuration )
        throws ContinuumStoreException
    {
        ContinuumProject project = new ContinuumProject();

        project.setName( name );
        project.setScmUrl( scmUrl );
        project.setNagEmailAddress( nagEmailAddress );
        project.setVersion( version );
        project.setBuilderId( builderId );
        project.setWorkingDirectory( workingDirectory );
        project.setState( ContinuumProjectState.NEW );
        project.setConfiguration( configuration );

        try
        {
            Object id = store.addContinuumProject( project );

            project = store.getContinuumProjectByJdoId( id, true );
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
            throw new ContinuumStoreException( "Error while removing project with id '" + projectId + "'.", e );
        }
    }

    public void updateProject( String projectId, String name, String scmUrl, String nagEmailAddress, String version,
                               Properties properties )
        throws ContinuumStoreException
    {
        try
        {
            store.begin();

            ContinuumProject project = store.getContinuumProject( projectId, false );

            project.setName( name );
            project.setScmUrl( scmUrl );
            project.setNagEmailAddress( nagEmailAddress );
            project.setVersion( version );

            store.commit();
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
            Collection projects = store.getContinuumProjectCollection( true, null, "name ascending" );

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
            ContinuumProject project = store.getContinuumProject( projectId, true );

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
            store.begin();

            ContinuumProject project = store.getContinuumProject( projectId, false );

            project.setState( ContinuumProjectState.BUILD_SIGNALED );

            ContinuumBuild build = new ContinuumBuild();

            build.setState( ContinuumProjectState.BUILD_SIGNALED );

            build.setStartTime( new Date().getTime() );

            build.setProject( project );

            Object id = store.addContinuumBuild( build );

            store.commit();

            build = store.getContinuumBuildByJdoId( id, true );

            return build.getId();
        }
        catch ( Exception e )
        {
            throw new ContinuumStoreException( "Error while creating continuum build for project: '" + projectId + "'.", e );
        }
    }

    public void setBuildResult( String buildId, int state, ContinuumBuildResult result, Throwable error )
        throws ContinuumStoreException
    {
        try
        {
            store.begin();

            ContinuumBuild build = store.getContinuumBuild( buildId, false );

            ContinuumProject project = build.getProject();

            project.setState( state );

            build.setState( state );

            build.setEndTime( new Date().getTime() );

            build.setError( throwableToString( error ) );

            store.commit();

            // ----------------------------------------------------------------------
            // This double commit seems to be needed for some reason. Not having it
            // seems to result in some foreign key constraint violation.
            // ----------------------------------------------------------------------

            store.begin();

            build = store.getContinuumBuild( buildId, false );

            build.setBuildResult( result );

            store.commit();
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
            store.begin();

            ContinuumBuild build = store.getContinuumBuild( buildId, false );

            Object projectOId = JDOHelper.getObjectId( build.getProject() );

            // TODO: This loading of the project is a bit of a hack, there has to be a better way of doing his.
            ContinuumProject project = store.getContinuumProjectByJdoId( projectOId, true );

            store.commit();

            build.setProject( null );
            project.getBuilds().clear();
            build.setProject( project );

            return build;
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
            List builds = store.getContinuumProject( projectId, true ).getBuilds();

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
            Collection builds = store.getContinuumBuildCollection( true, "this.project.id == \"" + projectId + "\"", "startTime descending" );

            return builds.iterator();
        }
        catch ( Exception e )
        {
            throw new ContinuumStoreException( "Error while getting builds for project id: '" + projectId + "'.", e );
        }
    }
}