package org.codehaus.continuum.store.hibernate; 

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

import java.net.URL;
import java.util.Iterator;
import java.util.List;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

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
import org.codehaus.plexus.hibernate.HibernateSessionService;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: HibernateContinuumStore.java,v 1.11 2004-10-06 13:47:05 trygvis Exp $
 */
public class HibernateContinuumStore
    extends AbstractContinuumStore
    implements Initializable, Startable
{
    /** @requirement */
    private HibernateSessionService hibernate;

    /** @requirement */
    private StoreTransactionManager txManager;

    private final static String HIBERNATE_CONFIGURATION = "hibernate.cfg.xml";

    // ----------------------------------------------------------------------
    // Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws Exception
    {
        PlexusUtils.assertRequirement( hibernate, HibernateSessionService.ROLE );
        PlexusUtils.assertRequirement( txManager, StoreTransactionManager.ROLE );
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
    // Database methods
    // ----------------------------------------------------------------------

    public void createDatabase()
        throws ContinuumStoreException
    {
        URL configuration = Thread.currentThread().getContextClassLoader().getResource( HIBERNATE_CONFIGURATION );

        if ( configuration == null )
        {
            throw new ContinuumStoreException( "Internal error: Could not find hibernate configuration: " + HIBERNATE_CONFIGURATION );
        }

        try
        {
            HibernateUtils.createDatabase( configuration );
        }
        catch( HibernateException ex )
        {
            throw new ContinuumStoreException( "Exception while initializing database.", ex );
        }
    }

    public void deleteDatabase()
        throws ContinuumStoreException
    {
        String name = "/hibernate.xml";

        URL configuration = Thread.currentThread().getContextClassLoader().getResource( name );

        if ( configuration == null )
        {
            throw new ContinuumStoreException( "Internal error: Could not find hibernate configuration: " + name );
        }

        try
        {
            HibernateUtils.deleteDatabase( configuration );
        }
        catch( HibernateException ex )
        {
            throw new ContinuumStoreException( "Exception while initializing database.", ex );
        }
    }

    // ----------------------------------------------------------------------
    // ContinuumProject
    // ----------------------------------------------------------------------

    public String addProject( String name, String scmConnection, String type )
        throws ContinuumStoreException
    {
        try
        {
            txManager.enter();

            Session session = getHibernateSession();

            ContinuumProject project = new GenericContinuumProject();

            project.setName( name );

            project.setScmConnection( scmConnection );

            project.setState( ContinuumProjectState.NEW );

            project.setType( type );

            session.save( project );

            txManager.leave();

            return project.getId();
        }
        catch( HibernateException ex )
        {
            txManager.rollback();

            throw new ContinuumStoreException( "Error while adding project.", ex );
        }
    }

    public void removeProject( String projectId )
        throws ContinuumStoreException
    {
        try
        {
            Session session = getHibernateSession();

            txManager.enter();

            session.delete( "from GenericContinuumBuild where projectId=?", projectId, Hibernate.STRING );

            ContinuumProject project = getProject( projectId );

            ProjectDescriptor descriptor = project.getDescriptor();

            // TODO: hibernate should take care of this.
            if ( descriptor != null )
            {
                session.delete( descriptor );
            }

            session.delete( project );

            txManager.leave();
        }
        catch( HibernateException ex )
        {
            txManager.rollback();

            throw new ContinuumStoreException( "Error while adding project.", ex );
        }
    }

    public void setProjectDescriptor( String projectId, ProjectDescriptor descriptor )
        throws ContinuumStoreException
    {
        try
        {
            Session session = hibernate.getSession();

            txManager.enter();

            ContinuumProject project = (ContinuumProject) session.load( GenericContinuumProject.class, projectId );

            descriptor.setProjectId( projectId );

            descriptor.setProject( project );

            project.setDescriptor( descriptor );

            session.save( descriptor );

            session.update( project );

            txManager.leave();
        }
        catch( HibernateException ex )
        {
            txManager.rollback();

            throw new ContinuumStoreException( "Error while setting the project descriptor.", ex );
        }
    }

    public void updateProjectDescriptor( String projectId, ProjectDescriptor descriptor )
        throws ContinuumStoreException
    {
        if ( descriptor == null )
        {
            throw new ContinuumStoreException( "Invalid parameters: The project descriptor cannot be null." );
        }

        try
        {
            Session session = hibernate.getSession();

            txManager.enter();

            ContinuumProject project = (ContinuumProject) session.load( GenericContinuumProject.class, projectId );

            ProjectDescriptor originalDescriptor = project.getDescriptor();

            if ( originalDescriptor != null )
            {
                project.setDescriptor( null );

                session.update( project );

                session.delete( originalDescriptor );

                // This flush && evict trick is required because hibernate 
                // caches the removed descriptor
                session.flush();

                session.evict( originalDescriptor );
            }

            project = (ContinuumProject) session.load( GenericContinuumProject.class, projectId );

            descriptor.setProjectId( projectId );

            descriptor.setProject( project );

            project.setDescriptor( descriptor );

            session.save( descriptor );

            session.update( project );

            txManager.leave();
        }
        catch( HibernateException ex )
        {
            txManager.rollback();

            throw new ContinuumStoreException( "Error while updating the project descriptor.", ex );
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
        }
        finally
        {
            txManager.leave();
        }
    }

    public Iterator getAllProjects()
        throws ContinuumStoreException
    {
        Session session = getHibernateSession();

        Iterator it;

        try
        {
            txManager.enter();

            it = session.find( "from " + ContinuumProject.class.getName() + " order by name" ).iterator();

            txManager.leave();
        }
        catch( HibernateException ex )
        {
            txManager.rollback();

            throw new ContinuumStoreException( "Error while loading all projects.", ex );
        }

        return it;
    }

    public Iterator findProjectsByName( String nameSearchPattern )
        throws ContinuumStoreException
    {
        Session session = getHibernateSession();

        List list;

        try
        {
            txManager.enter();

            list = session.find( "from GenericContinuumProject where name=? order by name", nameSearchPattern, Hibernate.STRING );

            txManager.leave();
        }
        catch( HibernateException ex )
        {
            txManager.rollback();

            throw new ContinuumStoreException( "Error while loading project.", ex );
        }

        return list.iterator();
    }

    public ContinuumProject getProject( String projectId )
        throws ContinuumStoreException
    {
        Session session = getHibernateSession();

        ContinuumProject continuumProject;

        try
        {
            txManager.enter();

            continuumProject = (ContinuumProject) session.load( GenericContinuumProject.class, projectId );

            txManager.leave();
        }
        catch( HibernateException ex )
        {
            txManager.rollback();

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
        txManager.enter();

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

            txManager.leave();
        }
        catch( HibernateException ex )
        {
            txManager.rollback();

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

            txManager.enter();

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

            txManager.leave();
        }
        catch( HibernateException ex )
        {
            txManager.rollback();

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

            txManager.enter();

            buildResult = (ContinuumBuild) session.load( GenericContinuumBuild.class, id );

            txManager.leave();
        }
        catch( HibernateException ex )
        {
            txManager.rollback();

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

            txManager.enter();

            List list = session.find( "from GenericContinuumBuild where projectId=? order by startTime", projectId, Hibernate.STRING );

            it = list.iterator();

            txManager.leave();
        }
        catch( HibernateException ex )
        {
            txManager.rollback();

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
