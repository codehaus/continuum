package org.codehaus.continuum;

import org.apache.maven.project.MavenProject;
import org.apache.maven.scm.manager.ScmManager;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: MavenProjectScm.java,v 1.6 2004-05-13 17:48:17 trygvis Exp $
 */
public class MavenProjectScm
{
    /** */
    private ScmManager scmManager;

    /**
     * The project must have a repository.
     * 
     * @param project The project.
     * @throws Exception
     */
    public MavenProjectScm( MavenProject project )
        throws ContinuumException
    {
/*
        Scm repository = project.getScm();

        if ( !project.hasScm() )
            throw new ContinuumException( "The project must have a repository." );

        try
        {
            scmManager = new DefaultScmManager( repository.getConnection() );
        }
        catch( ScmException ex )
        {
            throw new ContinuumException( "Exception while creating scm manager.", ex );
        }
*/
    }

    /**
     * Checks out the sources to the specified directory.
     * 
     * @param directory The base directory for the checkout.
     * @throws ContinuumException Thrown in case of a exception while checking out the sources.
     */
    public void checkout( String directory )
        throws ContinuumException
    {
/*
        try
        {
            scmManager.checkout( directory );
        }
        catch ( Exception e )
        {
            throw new ContinuumException( "Cannot checkout sources.", e );
        }
*/
    }

    /**
     * Updates the sources in the specified directory.
     * 
     * @param directory The base directory for the update.
     * @throws ContinuumException Thrown in case of a exception while updating the sources.
     */
    public void update( String directory )
        throws ContinuumException
    {
/*
        try
        {
            scmManager.update( directory );
        }
        catch ( Exception e )
        {
            throw new ContinuumException( "Cannot update sources.", e );
        }
*/
    }
}
