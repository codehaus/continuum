package org.codehaus.continuum.scm;

/*
 * The MIT License
 *
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

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.command.checkout.CheckOutScmResult;
import org.apache.maven.scm.command.update.UpdateScmResult;
import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.scm.repository.ScmRepository;
import org.apache.maven.scm.repository.ScmRepositoryException;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.utils.PlexusUtils;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

import java.io.File;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultContinuumScm.java,v 1.1.1.1 2005-02-17 22:23:52 trygvis Exp $
 */
public class DefaultContinuumScm
    extends AbstractLogEnabled
    implements ContinuumScm, Initializable
{
    /**
     * @requirement
     */
    private ScmManager scmManager;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws Exception
    {
        PlexusUtils.assertRequirement( scmManager, ScmManager.ROLE );
    }

    // ----------------------------------------------------------------------
    // ContinuumScm implementation
    // ----------------------------------------------------------------------

    public void checkOut( File workingDirectory, String scmUrl )
        throws ContinuumScmException
    {
        try
        {
            ScmRepository repository = scmManager.makeScmRepository( scmUrl );

            CheckOutScmResult result;

            synchronized ( this )
            {
                if ( !workingDirectory.exists() )
                {
                    if ( !workingDirectory.mkdirs() )
                    {
                        throw new ContinuumScmException( "Could not make directory: " + workingDirectory.getAbsolutePath() );
                    }
                }

                String tag = null;

                ScmFileSet fileSet = new ScmFileSet( workingDirectory );

                result = scmManager.checkOut( repository, fileSet, tag );
            }

            if ( !result.isSuccess() )
            {
                throw new ContinuumScmException( "Error while checking out the project.", result );
            }
        }
        catch ( ScmRepositoryException e )
        {
            throw new ContinuumScmException( "Cannot checkout sources.", e );
        }
        catch ( ScmException e )
        {
            throw new ContinuumScmException( "Cannot checkout sources.", e );
        }
    }

    /**
     * Checks out the sources to the specified directory.
     *
     * @param project The project to check out.
     * @throws ContinuumScmException Thrown in case of a exception while checking out the sources.
     */
    public void checkOutProject( ContinuumProject project )
        throws ContinuumScmException
    {
        String wd = project.getWorkingDirectory();

        if ( wd == null )
        {
            throw new ContinuumScmException( "The working directory for the project has to be set." );
        }

        checkOut( new File( wd ), project.getScmUrl() );
    }

    /**
     * Updates the sources in the specified directory.
     *
     * @param project The project to update.
     * @throws ContinuumScmException Thrown in case of a exception while updating the sources.
     */
    public boolean updateProject( ContinuumProject project )
        throws ContinuumScmException
    {
        try
        {
            File workingDirectory = new File( project.getWorkingDirectory() );

            getLogger().info( workingDirectory.getAbsolutePath() );

            if ( !workingDirectory.exists() )
            {
                throw new ContinuumScmException( "The working directory for the project doesn't exist (" + project.getWorkingDirectory() + ")." );
            }

            ScmRepository repository = scmManager.makeScmRepository( project.getScmUrl() );

            String tag = null;

            UpdateScmResult result;

            ScmFileSet fileSet = new ScmFileSet( workingDirectory );

            synchronized ( this )
            {
                result = scmManager.update( repository, fileSet, tag );
            }

            if ( !result.isSuccess() )
            {
                throw new ContinuumScmException( "Error while updating project.", result );
            }

            return result.getUpdatedFiles().size() > 0;
        }
        catch ( ScmRepositoryException ex )
        {
            throw new ContinuumScmException( "Error while update sources.", ex );
        }
        catch ( ScmException ex )
        {
            throw new ContinuumScmException( "Error while update sources.", ex );
        }
    }
}
