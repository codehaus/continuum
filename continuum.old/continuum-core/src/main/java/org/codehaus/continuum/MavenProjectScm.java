package org.codehaus.continuum;

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

import org.apache.maven.project.MavenProject;
import org.apache.maven.scm.manager.ScmManager;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: MavenProjectScm.java,v 1.7 2004-07-27 05:42:12 trygvis Exp $
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
