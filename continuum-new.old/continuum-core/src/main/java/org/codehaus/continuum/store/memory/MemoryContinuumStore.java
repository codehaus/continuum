package org.codehaus.continuum.store.memory;

/*
 * The MIT License
 *
 * Copyright (c) 2004, Jason van Zyl and Trygve Laugst�l
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

import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumBuildResult;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ContinuumProjectState;
import org.codehaus.continuum.store.AbstractContinuumStore;
import org.codehaus.continuum.store.ContinuumStoreException;
import org.codehaus.continuum.utils.Mutex;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

import java.util.Iterator;
import java.util.Properties;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: MemoryContinuumStore.java,v 1.1.1.1 2005-02-17 22:23:53 trygvis Exp $
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

    public String addProject( String name, String scmConnection, String nagEmailAddress, String version, String type, String workingDirectory, Properties configuration )
        throws ContinuumStoreException
    {
        try
        {
            enter();

            String projectId = database.addProject( name, scmConnection, nagEmailAddress, version, type, workingDirectory, configuration );

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

    public void setBuildResult( String id, ContinuumProjectState state, ContinuumBuildResult buildResult, Throwable error )
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
        throws ContinuumStoreException
    {
        lock.release();
    }

    private ContinuumStoreException rollback( ContinuumStoreException ex )
    {
        lock.release();

        return ex;
    }
}
