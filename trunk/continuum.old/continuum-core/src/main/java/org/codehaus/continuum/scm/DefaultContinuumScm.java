package org.codehaus.plexus.continuum;

/*
 * LICENSE
 */

import org.apache.maven.project.MavenProject;
import org.apache.maven.scm.manager.ScmManager;

import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultContinuumScm.java,v 1.1 2004-04-24 23:54:12 trygvis Exp $
 */
public class DefaultContinuumScm
    extends AbstractLogEnabled
    implements ContinuumScm, Initializable
{
    // requirements

    private ScmManager scmManager;

    ///////////////////////////////////////////////////////////////////////////
    // Lifecycle

    public void initialize()
        throws Exception
    {
        if ( scmManager == null )
            throw new PlexusConfigurationException( "Missing requirement: " + ScmManager.class.getName() );
    }

    ///////////////////////////////////////////////////////////////////////////
    // ContinuumScm implementation

    /**
     * Checks out the sources to the specified directory.
     * 
     * @param directory The base directory for the checkout.
     * @throws ContinuumException Thrown in case of a exception while checking out the sources.
     */
    public String checkout( MavenProject project, String directory )
        throws ContinuumException
    {
        try
        {
            scmManager.checkout( directory );

            return "";
        }
        catch ( Exception e )
        {
            throw new ContinuumException( "Cannot checkout sources.", e );
        }
    }

    /**
     * Updates the sources in the specified directory.
     * 
     * @param directory The base directory for the update.
     * @throws ContinuumException Thrown in case of a exception while updating the sources.
     */
    public String update( MavenProject project, String directory )
        throws ContinuumException
    {
        try
        {
            scmManager.update( directory );

            return "";
        }
        catch ( Exception e )
        {
            throw new ContinuumException( "Cannot update sources.", e );
        }
    }
}
