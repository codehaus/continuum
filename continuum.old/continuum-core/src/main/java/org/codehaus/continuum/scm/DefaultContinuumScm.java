package org.codehaus.continuum.scm;

/*
 * LICENSE
 */

import java.io.File;
import java.io.IOException;

import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.scm.repository.RepositoryInfo;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.utils.PlexusUtils;
import org.codehaus.continuum.utils.ScmUtils;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.util.FileUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultContinuumScm.java,v 1.8 2004-07-19 16:28:17 trygvis Exp $
 */
public class DefaultContinuumScm
    extends AbstractLogEnabled
    implements ContinuumScm, Initializable
{
    /** @default */
    private String checkoutDirectory;

    /** @requirement */
    private ScmManager scmManager;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws Exception
    {
        PlexusUtils.assertRequirement( scmManager, ScmManager.ROLE );

        PlexusUtils.assertConfiguration( checkoutDirectory, "checkout-directory" );

        File f = new File( checkoutDirectory );

        getLogger().info( "Using " + checkoutDirectory + " as checkout directory." );

        if ( !f.exists() )
        {
            getLogger().info( "Checkout directory does not exist, creating." );
            f.mkdirs();
        }
    }

    // ----------------------------------------------------------------------
    // ContinuumScm implementation
    // ----------------------------------------------------------------------

    public void clean( ContinuumProject project )
        throws ContinuumException
    {
        try
        {
            FileUtils.deleteDirectory( getProjectScmDirectory( project, checkoutDirectory ) );
        }
        catch( IOException ex )
        {
            throw new ContinuumException( "Exception while cleaning the directory.", ex );
        }
    }

    /**
     * Checks out the sources to the specified directory.
     * 
     * @param project The project to check out.
     * @throws ContinuumException Thrown in case of a exception while checking out the sources.
     */
    public String checkout( ContinuumProject project )
        throws ContinuumException
    {
        try
        {
            String dir = getProjectScmDirectory( project, checkoutDirectory );

            RepositoryInfo repositoryInfo = ScmUtils.createRepositoryInfo( project );

            synchronized( this )
            {
                scmManager.setRepositoryInfo( repositoryInfo );

                scmManager.checkout( dir );
            }

            // TODO: yes, this is CVS specific and pure bad
            String connection = repositoryInfo.getConnection();

            return dir + File.separator + 
                   connection.substring( connection.lastIndexOf( ":" ) + 1 );
        }
        catch ( Exception e )
        {
            throw new ContinuumException( "Cannot checkout sources.", e );
        }
    }

    /**
     * Updates the sources in the specified directory.
     * 
     * @param project The project to update.
     * @throws ContinuumException Thrown in case of a exception while updating the sources.
     */
    public synchronized String update( ContinuumProject project )
        throws ContinuumException
    {
        try
        {
            String dir = getProjectScmDirectory( project, checkoutDirectory );

            synchronized( this )
            {
                scmManager.setRepositoryInfo( ScmUtils.createRepositoryInfo( project ) );

                scmManager.update( dir );
            }

            return dir;
        }
        catch ( Exception e )
        {
            throw new ContinuumException( "Cannot update sources.", e );
        }
    }

    // ----------------------------------------------------------------------
    // Private
    // ----------------------------------------------------------------------

    private String getProjectScmDirectory( ContinuumProject project, String checkoutDirectory )
    {
        return checkoutDirectory + File.separator + project.getId();
    }
}
