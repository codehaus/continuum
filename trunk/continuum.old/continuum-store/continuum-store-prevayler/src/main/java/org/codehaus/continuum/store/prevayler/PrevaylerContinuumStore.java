package org.codehaus.continuum.store.prevayler;

/*
 * LICENSE
 */

import java.io.IOException;
import java.util.Iterator;

import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumBuildResult;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ContinuumProjectState;
import org.codehaus.continuum.project.ProjectDescriptor;
import org.codehaus.continuum.store.AbstractContinuumStore;
import org.codehaus.continuum.store.ContinuumStoreException;
import org.codehaus.continuum.store.tx.StoreTransactionManager;
import org.codehaus.continuum.utils.PlexusUtils;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.util.FileUtils;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.Transaction;
import org.prevayler.TransactionWithQuery;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: PrevaylerContinuumStore.java,v 1.1 2004-10-06 13:52:43 trygvis Exp $
 */
public class PrevaylerContinuumStore
    extends AbstractContinuumStore
    implements Initializable, Startable
{
    /** @requirement */
    private StoreTransactionManager txManager;

    /** @configuration */
    private String databaseDirectory;

    private Prevayler prevayler;

    private ContinuumDatabase database;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws Exception
    {
        PlexusUtils.assertRequirement( txManager, StoreTransactionManager.ROLE );
        PlexusUtils.assertConfiguration( databaseDirectory, "database-directory" );
    }

    public void start()
        throws ContinuumStoreException
    {
        startPrevayler();
    }

    public void stop()
        throws ContinuumStoreException
    {
        stopPrevayler();
    }

    // ----------------------------------------------------------------------
    // Database Management
    // ----------------------------------------------------------------------

    /** */
    public void createDatabase()
        throws ContinuumStoreException
    {
        stopPrevayler();

        try
        {
            FileUtils.deleteDirectory( databaseDirectory );
        }
        catch( IOException ex )
        {
            throw new ContinuumStoreException( "Error while cleaning database directory.", ex );
        }

        startPrevayler();
    }

    /** */
    public void deleteDatabase()
        throws ContinuumStoreException
    {
        stopPrevayler();

        try
        {
            FileUtils.deleteDirectory( databaseDirectory );
        }
        catch( IOException ex )
        {
            throw new ContinuumStoreException( "Error while cleaning database directory.", ex );
        }
    }

    // ----------------------------------------------------------------------
    // Project Management
    // ----------------------------------------------------------------------

    /** */
    public String addProject( String name, String scmConnection, String type )
        throws ContinuumStoreException
    {
        String id = (String) executeQuery( new AddProjectTx( name, scmConnection, type ) );

        getLogger().info( "Added project '" + name + "', id:" + id );

        return id;
    }

    final static class AddProjectTx
        extends AbstractContinuumPrevaylerTransactionWithQuery
    {
        private String name;

        private String scmConnection;

        private String type;

        public AddProjectTx( String name, String scmConnection, String type )
        {
            this.name = name;
            this.scmConnection = scmConnection;
            this.type = type;
        }

        public Object execute( ContinuumDatabase database ) throws Exception
        {
            return database.addProject( name, scmConnection, type );
        }
    }

    /** */
    public void removeProject( String projectId )
        throws ContinuumStoreException
    {
        execute( new RemoveProjectTx( projectId ) );
    }

    private static class RemoveProjectTx
        extends AbstractContinuumPrevaylerTransaction
    {
        private String projectId;

        public RemoveProjectTx( String projectId )
        {
            this.projectId = projectId;
        }

        public void execute( ContinuumDatabase database )
            throws ContinuumStoreException
        {
            database.removeProject( projectId );
        }
    }

    /** */
    public void setProjectDescriptor( String projectId, ProjectDescriptor descriptor )
        throws ContinuumStoreException
    {
        execute( new SetProjectDescriptorTx( projectId, descriptor ) );
    }

    final static class SetProjectDescriptorTx
        extends AbstractContinuumPrevaylerTransaction
    {
        private String projectId;

        private ProjectDescriptor descriptor;

        public SetProjectDescriptorTx( String projectId, ProjectDescriptor descriptor )
        {
            this.projectId = projectId;
            this.descriptor = descriptor;
        }

        public void execute( ContinuumDatabase database )
            throws ContinuumStoreException
        {
            database.setProjectDescriptor( projectId, descriptor );
        }
    }

    /** */
    public void updateProjectDescriptor( String projectId, ProjectDescriptor descriptor )
        throws ContinuumStoreException
    {
        execute( new UpdateProjectDescriptorTx( projectId, descriptor ) );
    }

    private static class UpdateProjectDescriptorTx
        extends AbstractContinuumPrevaylerTransaction
    {
        private String projectId;
        private ProjectDescriptor descriptor;

        public UpdateProjectDescriptorTx( String projectId, ProjectDescriptor descriptor )
        {
            this.projectId = projectId;
            this.descriptor = descriptor;
        }
    
        public void execute( ContinuumDatabase database )
            throws ContinuumStoreException
        {
            database.updateProjectDescriptor( projectId, descriptor );
        }
    }

    /** */
    public void updateProject( String projectId, String name, String scmUrl )
        throws ContinuumStoreException
    {
        execute( new UpdateProjectTx( projectId, name, scmUrl ) );
    }

    private static class UpdateProjectTx
        extends AbstractContinuumPrevaylerTransaction
    {
        private String projectId;
        private String name;
        private String scmUrl;

        public UpdateProjectTx( String projectId, String name, String scmUrl )
        {
            this.projectId = projectId;
            this.name = name;
            this.scmUrl = scmUrl;
        }

        public void execute( ContinuumDatabase database )
            throws ContinuumStoreException
        {
            database.updateProject( projectId, name, scmUrl );
        }
    }

    /** */
    public Iterator getAllProjects()
        throws ContinuumStoreException
    {
        return (Iterator) executeQuery( new GetAllProjectsTx() );
    }

    private static class GetAllProjectsTx
        extends AbstractContinuumPrevaylerTransactionWithQuery
    {
        public Object execute( ContinuumDatabase database )
            throws ContinuumStoreException
        {
            return database.getAllProjects();
        }
    }

    /** */
    public Iterator findProjectsByName( String nameSearchPattern )
        throws ContinuumStoreException
    {
        return (Iterator) executeQuery( new FindProjectsByNameTx( nameSearchPattern ) );
    }

    private static class FindProjectsByNameTx
        extends AbstractContinuumPrevaylerTransactionWithQuery
    {
        private String nameSearchPattern;

        public FindProjectsByNameTx( String nameSearchPattern )
        {
            this.nameSearchPattern = nameSearchPattern;
        }

        public Object execute( ContinuumDatabase database )
            throws ContinuumStoreException
        {
            return database.findProjectsByName( nameSearchPattern );
        }
    }

    /** */
    public ContinuumProject getProject( String projectId )
        throws ContinuumStoreException
    {
        return (ContinuumProject) executeQuery( new GetProjectTx( projectId ) );
    }

    private static class GetProjectTx
        extends AbstractContinuumPrevaylerTransactionWithQuery
    {
        private String projectId;

        public GetProjectTx( String projectId )
        {
            this.projectId = projectId;
        }

        public Object execute( ContinuumDatabase database )
            throws ContinuumStoreException
        {
            return database.getProject( projectId );
        }
    }

    // ----------------------------------------------------------------------
    // Build Management
    // ----------------------------------------------------------------------

    /** */
    public String createBuild( String projectId )
        throws ContinuumStoreException
    {
        return (String) executeQuery( new CreateBuildTx( projectId ) );
    }

    private static class CreateBuildTx
        extends AbstractContinuumPrevaylerTransactionWithQuery
    {
        private String projectId;

        public CreateBuildTx( String projectId )
        {
            this.projectId = projectId;
        }

        public Object execute( ContinuumDatabase database )
            throws ContinuumStoreException
        {
            return database.createBuild( projectId );
        }
    }

    /** */
    public ContinuumBuild getBuild( String buildId )
        throws ContinuumStoreException
    {
        return (ContinuumBuild) executeQuery( new GetBuildTx( buildId ) );
    }

    private static class GetBuildTx
        extends AbstractContinuumPrevaylerTransactionWithQuery
    {
        private String buildId;

        public GetBuildTx( String buildId )
        {
            this.buildId = buildId;
        }

        public Object execute( ContinuumDatabase database )
            throws ContinuumStoreException
        {
            return database.getBuild( buildId );
        }
    }

    /** */
    public void setBuildResult( String buildId, ContinuumProjectState state, ContinuumBuildResult result, Throwable error )
        throws ContinuumStoreException
    {
        execute( new SetBuildResultTx( buildId, state, result, error ) );
    }

    private static class SetBuildResultTx
        extends AbstractContinuumPrevaylerTransaction
    {
        private String buildId;
        private ContinuumProjectState state;
        private ContinuumBuildResult result;
        private Throwable error;

        public SetBuildResultTx( String buildId, ContinuumProjectState state, ContinuumBuildResult result, Throwable error )
        {
            this.buildId = buildId;
            this.state = state;
            this.result = result;
            this.error = error;
        }

        public void execute( ContinuumDatabase database )
            throws ContinuumStoreException
        {
            database.setBuildResult( buildId, state, result, error );
        }
    }

    /** */
    public Iterator getBuildsForProject( String projectId, int start, int end )
        throws ContinuumStoreException
    {
        return (Iterator) executeQuery( new GetBuildsForProjectTx( projectId, start, end ) );
    }

    private static class GetBuildsForProjectTx
        extends AbstractContinuumPrevaylerTransactionWithQuery
    {
        private String projectId;
        private int start;
        private int end;

        public GetBuildsForProjectTx( String projectId, int start, int end )
        {
            this.projectId = projectId;
            this.start = start;
            this.end = end;
        }

        public Object execute( ContinuumDatabase database )
            throws ContinuumStoreException
        {
            return database.getBuildsForProject( projectId, start, end );
        }
    }
    
    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private Object executeQuery( TransactionWithQuery tx )
        throws ContinuumStoreException
    {
        try
        {
            txManager.enter();

            Object value = prevayler.execute( tx );

            txManager.leave();

            return value;
        }
        catch( Exception ex )
        {
            txManager.rollback();

            throw new ContinuumStoreException( "Error while executing transaction.", ex );
        }
    }

    private void execute( Transaction tx )
        throws ContinuumStoreException
    {
        txManager.enter();

        try
        {
            prevayler.execute( tx );

            txManager.leave();
        }
        catch( Exception ex )
        {
            txManager.rollback();

            throw new ContinuumStoreException( "Error while executing transaction.", ex );
        }
    }

    private void startPrevayler()
        throws ContinuumStoreException
    {
        try
        {
            prevayler = PrevaylerFactory.createPrevayler( new ContinuumDatabase(), databaseDirectory );
        }
        catch( ClassNotFoundException ex )
        {
            throw new ContinuumStoreException( "Error while initializing prevayler.", ex );
        }
        catch( IOException ex )
        {
            throw new ContinuumStoreException( "Error while initializing prevayler.", ex );
        }

        database = (ContinuumDatabase) prevayler.prevalentSystem();
    }

    private void stopPrevayler()
        throws ContinuumStoreException
    {
        try
        {
            prevayler.close();
        }
        catch( Exception ex )
        {
            throw new ContinuumStoreException( "Error while stopping prevayler.", ex );
        }
    }
}
