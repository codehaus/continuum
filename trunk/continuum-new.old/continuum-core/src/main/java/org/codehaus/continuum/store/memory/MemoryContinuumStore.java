package org.codehaus.continuum.store.memory;

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

import java.util.Iterator;
import java.util.Properties;

import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumBuildResult;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.store.AbstractContinuumStore;
import org.codehaus.continuum.store.ContinuumStoreException;
import org.codehaus.continuum.utils.Mutex;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: MemoryContinuumStore.java,v 1.3 2005-03-10 00:05:54 trygvis Exp $
 */
public class MemoryContinuumStore
    extends AbstractContinuumStore
    implements Initializable
{
    private ContinuumDatabase database;

    public MemoryContinuumStore()
    {
    }

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws Exception
    {
        createDatabase();
    }

    // ----------------------------------------------------------------------
    // Database methods
    // ----------------------------------------------------------------------

    public void createDatabase()
        throws ContinuumStoreException
    {
        database = new ContinuumDatabase();
    }

    public void deleteDatabase()
        throws ContinuumStoreException
    {
        database = null;
    }

    // ----------------------------------------------------------------------
    // ContinuumProject
    // ----------------------------------------------------------------------

    public String addProject( String name, String scmUrl, String nagEmailAddress, String version, String builderId, String workingDirectory, Properties configuration )
        throws ContinuumStoreException
    {
        try
        {
            enter();

            String projectId = database.addProject( name, scmUrl, nagEmailAddress, version, builderId, workingDirectory, configuration );

            leave();

            return projectId;
        }
        catch ( ContinuumStoreException ex )
        {
            throw rollback( ex );
        }
    }

    public void removeProject( String projectId )
        throws ContinuumStoreException
    {
        try
        {
            enter();

            database.removeProject( projectId );

            leave();
        }
        catch ( ContinuumStoreException ex )
        {
            throw rollback( ex );
        }
    }

    public void updateProject( String projectId, String name, String scmUrl, String nagEmailAddress, String version, Properties p )
        throws ContinuumStoreException
    {
        try
        {
            enter();

            database.updateProject( projectId, name, scmUrl, nagEmailAddress, version, p );

            leave();
        }
        catch ( ContinuumStoreException ex )
        {
            throw rollback( ex );
        }
    }

    public void setWorkingDirectory( String projectId, String workingDirectory )
        throws ContinuumStoreException
    {
        try
        {
            enter();

            database.setWorkingDirectory( projectId, workingDirectory );

            leave();
        }
        catch ( ContinuumStoreException ex )
        {
            throw rollback( ex );
        }
    }

    public Iterator getAllProjects()
        throws ContinuumStoreException
    {
        try
        {
            enter();

            Iterator it = database.getAllProjects();

            leave();

            return it;
        }
        catch ( ContinuumStoreException ex )
        {
            throw rollback( ex );
        }
    }

    public Iterator findProjectsByName( String nameSearchPattern )
        throws ContinuumStoreException
    {
        try
        {
            enter();

            Iterator it = database.findProjectsByName( nameSearchPattern );

            leave();

            return it;
        }
        catch ( ContinuumStoreException ex )
        {
            throw rollback( ex );
        }
    }

    public ContinuumProject getProject( String projectId )
        throws ContinuumStoreException
    {
        try
        {
            enter();

            ContinuumProject project = database.getProject( projectId );

            leave();

            return project;
        }
        catch ( ContinuumStoreException ex )
        {
            throw rollback( ex );
        }
    }

//    private ContinuumProject findProjectByName( String name )
//        throws ContinuumStoreException
//    {
//        try
//        {
//            enter();
//
//            ContinuumProject project = database.findProjectByName( name );
//
//            leave();
//
//            return project;
//        }
//        catch( ContinuumStoreException ex )
//        {
//            throw rollback( ex );
//        }
//    }

    // ----------------------------------------------------------------------
    // Build
    // ----------------------------------------------------------------------

    public String createBuild( String projectId )
        throws ContinuumStoreException
    {
        try
        {
            enter();

            String buildId = database.createBuild( projectId );

            leave();

            return buildId;
        }
        catch ( ContinuumStoreException ex )
        {
            throw rollback( ex );
        }
    }

    public void setBuildResult( String id, int state, ContinuumBuildResult buildResult, Throwable error )
        throws ContinuumStoreException
    {
        try
        {
            enter();

            database.setBuildResult( id, state, buildResult, error );

            leave();
        }
        catch ( ContinuumStoreException ex )
        {
            throw rollback( ex );
        }
    }

    public ContinuumBuild getBuild( String buildId )
        throws ContinuumStoreException
    {
        try
        {
            enter();

            ContinuumBuild build = database.getBuild( buildId );

            leave();

            return build;
        }
        catch ( ContinuumStoreException ex )
        {
            throw rollback( ex );
        }
    }

    public ContinuumBuild getLatestBuildForProject( String projectId )
        throws ContinuumStoreException
    {
        try
        {
            enter();

            ContinuumBuild build = database.getLatestBuildForProject( projectId );

            leave();

            return build;
        }
        catch ( ContinuumStoreException ex )
        {
            throw rollback( ex );
        }
    }

    public Iterator getBuildsForProject( String projectId, int start, int end )
        throws ContinuumStoreException
    {
        try
        {
            enter();

            Iterator builds = database.getBuildsForProject( projectId, start, end );

            leave();

            return builds;
        }
        catch ( ContinuumStoreException ex )
        {
            throw rollback( ex );
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private Mutex lock = new Mutex();

    private void enter()
        throws ContinuumStoreException
    {
        try
        {
            lock.acquire();
        }
        catch ( InterruptedException ex )
        {
            throw new ContinuumStoreException( "Interrupted while locking.", ex );
        }
    }

    private void leave()
    {
        lock.release();
    }

    private ContinuumStoreException rollback( ContinuumStoreException ex )
    {
        lock.release();

        return ex;
    }
}
