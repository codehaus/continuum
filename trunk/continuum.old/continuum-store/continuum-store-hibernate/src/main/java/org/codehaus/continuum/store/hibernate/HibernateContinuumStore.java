package org.codehaus.continuum.store.hibernate;

/*
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

import java.util.Iterator;
import java.util.List;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;

import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumBuildResult;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ContinuumProjectState;
import org.codehaus.continuum.project.GenericContinuumBuild;
import org.codehaus.continuum.project.GenericContinuumProject;
import org.codehaus.continuum.project.ProjectDescriptor;
import org.codehaus.continuum.store.AbstractContinuumStore;
import org.codehaus.continuum.store.ContinuumStoreException;
import org.codehaus.continuum.utils.PlexusUtils;
import org.codehaus.plexus.hibernate.HibernateSessionService;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: HibernateContinuumStore.java,v 1.7 2004-07-27 05:42:15 trygvis Exp $
 */
public class HibernateContinuumStore
    extends AbstractContinuumStore
    implements Initializable, Startable
{
    private HibernateSessionService hibernate;

    private Transaction tx;

    // ----------------------------------------------------------------------
    // Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws Exception
    {
        PlexusUtils.assertRequirement( hibernate, HibernateSessionService.ROLE );
    }

    public void start()
        throws Exception
    {
        getLogger().info( "Testing connection." );

        hibernate.getSession();

        hibernate.closeSession();
    }

    public void stop()
        throws Exception
    {
        getLogger().info( "Stopping the hibernate store." );

        hibernate.closeSession();
    }

    // ----------------------------------------------------------------------
    // Transaction handling
    // ----------------------------------------------------------------------

    public void beginTransaction()
        throws ContinuumStoreException
    {
        if ( tx != null )
        {
            throw new ContinuumStoreException( "A transaction is already in progress." );
        }

        try
        {
            Session session = getHibernateSession();

            tx = session.beginTransaction();
        }
        catch( HibernateException ex )
        {
            throw new ContinuumStoreException( "Exception while beginning transaction.", ex );
        }
    }

    public void commitTransaction()
        throws ContinuumStoreException
    {
        if ( tx == null )
        {
            throw new ContinuumStoreException( "No transaction has been started." );
        }

        try
        {
            tx.commit();
        }
        catch( HibernateException ex )
        {
            throw new ContinuumStoreException( "Exception while beginning transaction.", ex );
        }
        finally
        {
            closeSession();

            tx = null;
        }
    }

    public void rollbackTransaction()
        throws ContinuumStoreException
    {
        if ( tx == null )
        {
            throw new ContinuumStoreException( "No transaction has been started." );
        }

        try
        {
            tx.rollback();
        }
        catch( HibernateException ex )
        {
            throw new ContinuumStoreException( "Exception while beginning transaction.", ex );
        }
        finally
        {
            closeSession();

            tx = null;
        }
    }

    // ----------------------------------------------------------------------
    // ContinuumProject
    // ----------------------------------------------------------------------

    public String addProject( String name, String scmConnection, String type )
        throws ContinuumStoreException
    {
        ContinuumProject project = new GenericContinuumProject();

        project.setName( name );

        project.setScmConnection( scmConnection );

        project.setState( ContinuumProjectState.NEW );

        project.setType( type );

        try
        {
            Session session = getHibernateSession();

            session.save( project );
        }
        catch( HibernateException ex )
        {
            throw new ContinuumStoreException( "Error while adding project.", ex );
        }

        return project.getId();
    }

    public void setProjectDescriptor( String projectId, ProjectDescriptor descriptor )
        throws ContinuumStoreException
    {
        try
        {
            Session session = hibernate.getSession();

            ContinuumProject project = (ContinuumProject) session.load( GenericContinuumProject.class, projectId );

            descriptor.setProjectId( projectId );

            descriptor.setProject( project );

            project.setDescriptor( descriptor );

            session.save( descriptor );

            session.update( project );
        }
        catch( HibernateException ex )
        {
            throw new ContinuumStoreException( "Error while setting the project descriptor.", ex );
        }
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
        Session session = getHibernateSession();

        Iterator it;

        try
        {
            it = session.find( "from " + ContinuumProject.class.getName() ).iterator();
        }
        catch( HibernateException ex )
        {
            throw new ContinuumStoreException( "Error while loading all projects.", ex );
        }

        return it;
    }

    public ContinuumProject getProject( String projectId )
        throws ContinuumStoreException
    {
        Session session = getHibernateSession();

        ContinuumProject continuumProject;

        try
        {
            continuumProject = (ContinuumProject) session.load( GenericContinuumProject.class, projectId );
        }
        catch( HibernateException ex )
        {
            throw new ContinuumStoreException( "Error while loading project.", ex );
        }

        return continuumProject;
    }

    // ----------------------------------------------------------------------
    // Build
    // ----------------------------------------------------------------------

    public String createBuild( String projectId )
        throws ContinuumStoreException
    {
        GenericContinuumProject project = getGenericProject( projectId );

        GenericContinuumBuild build = new GenericContinuumBuild();

        project.setState( ContinuumProjectState.BUILD_SIGNALED );

        build.setProject( project );

        build.setState( ContinuumProjectState.BUILD_SIGNALED );

        build.setStartTime( System.currentTimeMillis() );

        try
        {
            Session session = getHibernateSession();

            session.update( project );

            session.save( build );
        }
        catch( HibernateException ex )
        {
            throw new ContinuumStoreException( "Error while creating build result.", ex );
        }

        return build.getId();
    }

    public void setBuildResult( String id, ContinuumProjectState state, ContinuumBuildResult buildResult, Throwable error )
        throws ContinuumStoreException
    {
        if ( state == null )
        {
            throw new NullPointerException( "'state' cannot be null." );
        }

        getLogger().warn( "Setting build " + id + " state to " + state );

        try
        {
            Session session = getHibernateSession();

            GenericContinuumBuild build = getGenericBuild( id );

            GenericContinuumProject project = (GenericContinuumProject)build.getProject();

            build.setState( state );

            build.setEndTime( System.currentTimeMillis() );

            if ( error != null )
            {
                build.setError( error );
            }

            if ( buildResult != null )
            {
                build.setBuildResult( buildResult );

                session.save( buildResult );
            }

            project.setState( state );

            session.update( build );

            session.update( project );
        }
        catch( HibernateException ex )
        {
            throw new ContinuumStoreException( "Error while setting the build result.", ex );
        }
    }

    public ContinuumBuild getBuild( String id )
        throws ContinuumStoreException
    {
        ContinuumBuild buildResult;

        try
        {
            Session session = getHibernateSession();

            buildResult = (ContinuumBuild) session.load( GenericContinuumBuild.class, id );
        }
        catch( HibernateException ex )
        {
            throw new ContinuumStoreException( "Error while loading build.", ex );
        }

        return buildResult;
    }

    // TODO: Implement start and end
    public Iterator getBuildsForProject( String projectId, int start, int end )
        throws ContinuumStoreException
    {
        Iterator it;

        try
        {
            Session session = getHibernateSession();

            List list = session.find( "from GenericContinuumBuild where projectId=?", projectId, Hibernate.STRING );

            it = list.iterator();
        }
        catch( HibernateException ex )
        {
            throw new ContinuumStoreException( "Error while loading the builds for the project.", ex );
        }

        return it;
    }

    // ----------------------------------------------------------------------
    // Private
    // ----------------------------------------------------------------------

    private Session getHibernateSession()
        throws ContinuumStoreException
    {
        try
        {
            return hibernate.getSession();
        }
        catch( HibernateException ex )
        {
            throw new ContinuumStoreException( "Exception while getting hibernate session.", ex );
        }
    }

    private void closeSession()
    {
        try
        {
            hibernate.closeSession();
        }
        catch( HibernateException ex )
        {
            getLogger().warn( "Exception while closing hibernate session.", ex );
        }
    }
}