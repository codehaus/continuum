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

import org.apache.maven.scm.command.checkout.CheckOutScmResult;
import org.apache.maven.scm.command.update.UpdateScmResult;
import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.scm.repository.ScmRepository;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.utils.PlexusUtils;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.util.FileUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultContinuumScm.java,v 1.14 2004-10-24 20:39:07 trygvis Exp $
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
            FileUtils.deleteDirectory( getProjectScmDirectory( project ) );
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
            File workingDirectory = getProjectScmDirectory( project );

            ScmRepository repository = scmManager.makeScmRepository( project.getScmUrl() );

            CheckOutScmResult result;

            synchronized( this )
            {
                if ( !workingDirectory.exists() )
                {
                    FileUtils.mkdir( workingDirectory.getAbsolutePath() );
                }

                String tag = null;

                result = scmManager.checkOut( repository, workingDirectory, tag );
            }

            if ( !result.isSuccess() )
            {
                throw new ContinuumException( "Error while checking out the project: " + result.getMessage() );
            }

            // TODO: yes, this is CVS specific and pure bad
            String url = repository.getScmSpecificUrl2();

            return new File( workingDirectory, url.substring( url.lastIndexOf( ":" ) + 1 ) );
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
            File dir = getProjectScmDirectory( project );

            ScmRepository repository = scmManager.makeScmRepository( project.getScmUrl() );

            String tag = null;

            UpdateScmResult result;

            synchronized( this )
            {
                result = scmManager.update( repository, dir, tag );
            }

            if ( !result.isSuccess() )
            {
                throw new ContinuumException( "Error while updating repository: " + result.getMessage() );
            }

            return dir;
        }
        catch ( Exception ex )
        {
            throw new ContinuumException( "Error while update sources.", ex );
        }
    }

    // ----------------------------------------------------------------------
    // Private
    // ----------------------------------------------------------------------

    private File getProjectScmDirectory( ContinuumProject project )
    {
        return new File( checkoutDirectory, project.getId() );
    }
}
