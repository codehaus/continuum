package org.codehaus.continuum.store.hibernate;

/*
 * LICENSE
 */

import java.util.Iterator;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;

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

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: HibernateContinuumStore.java,v 1.1 2004-07-07 02:34:37 trygvis Exp $
 */
public class HibernateContinuumStore
    extends AbstractContinuumStore
    implements Initializable
{
    private HibernateSessionService hibernate;

    // ----------------------------------------------------------------------
    // Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws Exception
    {
        PlexusUtils.assertRequirement( hibernate, HibernateSessionService.ROLE );
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

            Transaction tx = session.beginTransaction();

            session.save( project );

            tx.commit();
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

            Transaction tx = session.beginTransaction();

            ContinuumProject project = (ContinuumProject) session.load( GenericContinuumProject.class, projectId );

            descriptor.setProjectId( projectId );

            descriptor.setProject( project );

            project.setDescriptor( descriptor );

            session.save( descriptor );

            session.update( project );

            tx.commit();
        }
        catch( HibernateException ex )
        {
            throw new ContinuumStoreException( "Error while setting the project descriptor.", ex );
        }
    }

    public Iterator getAllProjects()
        throws ContinuumStoreException
    {
        throw new ContinuumStoreException( "NOT IMPLEMENTED." );
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

            Transaction tx = session.beginTransaction();

            session.save( buildResult );

            tx.commit();
        }
        catch( HibernateException ex )
        {
            throw new ContinuumStoreException( "Error while storing project.", ex );
        }

        return buildResult.getBuildId();
    }

    public void setBuildResult( String buildId, int state, Throwable error )
        throws ContinuumStoreException
    {
        try
        {
            Session session = getHibernateSession();

            Transaction tx = session.beginTransaction();

            BuildResult buildResult = getBuildResult( buildId );

            buildResult.setState( state );

            buildResult.setEndTime( System.currentTimeMillis() );

            buildResult.setError( error );

            session.update( buildResult );

            tx.commit();
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
