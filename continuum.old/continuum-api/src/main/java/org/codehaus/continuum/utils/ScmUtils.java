package org.codehaus.continuum.utils;

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

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.repository.RepositoryInfo;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.project.ContinuumProject;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ScmUtils.java,v 1.2 2004-07-27 05:42:11 trygvis Exp $
 */
public class ScmUtils
{
    private ScmUtils()
    {
    }

    public static RepositoryInfo createRepositoryInfo( ContinuumProject project )
        throws ContinuumException, ScmException
    {
        RepositoryInfo info = new RepositoryInfo();

        String url = project.getScmConnection();

        if ( url == null )
        {
            throw new ContinuumException( "Missing scm.developerConnection and scm.connection element from the project descriptor." );
        }

        info.setUrl( url );

        return info;
    }
}
