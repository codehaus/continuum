package org.codehaus.continuum.scm;

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
 * @version $Id: DefaultContinuumScm.java,v 1.10 2004-07-27 05:42:13 trygvis Exp $
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
    public File checkout( ContinuumProject project )
        throws ContinuumException
    {
        try
        {
            File workingDirectory = getProjectScmDirectory( project, checkoutDirectory );

            RepositoryInfo repositoryInfo = ScmUtils.createRepositoryInfo( project );

            synchronized( this )
            {
                scmManager.setRepositoryInfo( repositoryInfo );

                if ( !workingDirectory.exists() )
                {
                    FileUtils.mkdir( workingDirectory.getAbsolutePath() );
                }

                scmManager.checkout( workingDirectory.getAbsolutePath() );
            }

            // TODO: yes, this is CVS specific and pure bad
            String connection = repositoryInfo.getConnection();

            return new File( workingDirectory, connection.substring( connection.lastIndexOf( ":" ) + 1 ) );
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
    public synchronized File update( ContinuumProject project )
        throws ContinuumException
    {
        try
        {
            File dir = getProjectScmDirectory( project, checkoutDirectory );

            synchronized( this )
            {
                scmManager.setRepositoryInfo( ScmUtils.createRepositoryInfo( project ) );

                scmManager.update( dir.getAbsolutePath() );
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

    private File getProjectScmDirectory( ContinuumProject project, String checkoutDirectory )
    {
        return new File( checkoutDirectory, project.getId() );
    }
}
