package org.codehaus.continuum.store.hibernate;

/*
 * LICENSE
 */

import java.util.Iterator;
import java.util.List;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;

import org.apache.maven.ExecutionResponse;

import org.codehaus.continuum.project.BuildResult;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.GenericBuildResult;
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
 * @version $Id: HibernateContinuumStore.java,v 1.5 2004-07-19 16:39:33 trygvis Exp $
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

//            hibernate.closeSession();
        }
        catch( HibernateException ex )
        {
            throw new ContinuumStoreException( "Exception while beginning transaction.", ex );
        }
        finally
        {
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

            hibernate.closeSession();
        }
        catch( HibernateException ex )
        {
            throw new ContinuumStoreException( "Exception while beginning transaction.", ex );
        }
        finally
        {
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

        project.setState( ContinuumProject.PROJECT_STATE_NEW );

        project.setType( type );

        try
        {
            Session session = getHibernateSession();

            session.save( project );
        }
        catch( HibernateException ex )
        {
            throw new ContinuumStoreException( "Error while storing project.", ex );
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

    public Iterator getAllProjects()
        throws ContinuumStoreException
    {
        Session session = getHibernateSession();

        Iterator it;

        try
        {
//          it = session.find( "select cp from ContinuumProject as cp where 1=1" ).iterator();
            it = session.find( "from " + ContinuumProject.class.getName() ).iterator();
        }
        catch( HibernateException ex )
        {
            throw new ContinuumStoreException( "Error while querying database.", ex );
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
            throw new ContinuumStoreException( "Error while storing project.", ex );
        }

        return continuumProject;
    }

    // ----------------------------------------------------------------------
    // Build
    // ----------------------------------------------------------------------

    public String createBuildResult( String projectId )
        throws ContinuumStoreException
    {
        ContinuumProject project = getProject( projectId );

        GenericBuildResult buildResult = new GenericBuildResult();

        buildResult.setProject( project );

        buildResult.setState( BuildResult.BUILD_BUILDING );

        buildResult.setStartTime( System.currentTimeMillis() );

        try
        {
            Session session = getHibernateSession();

            session.save( buildResult );

            getLogger().info( "Saved: " + buildResult.getBuildId() );
            getLogger().info( "Saved: " + buildResult.getProject().getId() );
        }
        catch( HibernateException ex )
        {
            throw new ContinuumStoreException( "Error while storing project.", ex );
        }

        return buildResult.getBuildId();
    }

    public void setBuildResult( String buildId, int state, Throwable error, ExecutionResponse exectionResponse )
        throws ContinuumStoreException
    {
        try
        {
            Session session = getHibernateSession();

            BuildResult buildResult = getBuildResult( buildId );

            buildResult.setState( state );

            buildResult.setEndTime( System.currentTimeMillis() );

            buildResult.setError( error );

            session.update( buildResult );
        }
        catch( HibernateException ex )
        {
            throw new ContinuumStoreException( "Error while storing project.", ex );
        }
    }

    public BuildResult getBuildResult( String buildId )
        throws ContinuumStoreException
    {
        BuildResult buildResult;

        try
        {
            Session session = getHibernateSession();

            buildResult = (BuildResult) session.load( GenericBuildResult.class, buildId );
        }
        catch( HibernateException ex )
        {
            throw new ContinuumStoreException( "Error while storing project.", ex );
        }

        return buildResult;
    }

    // TODO: Implement start and end
    public Iterator getBuildResultsForProject( String projectId, int start, int end )
        throws ContinuumStoreException
    {
        Iterator it;

        getLogger().warn( "projectId: " + projectId );
        try
        {
            Session session = getHibernateSession();

            List list = session.find( "from GenericBuildResult where projectId=?", projectId, Hibernate.STRING );

            it = list.iterator();
        }
        catch( HibernateException ex )
        {
            throw new ContinuumStoreException( "Error while storing project.", ex );
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
}
