package org.codehaus.continuum.test.scm;

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

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.repository.AbstractRepository;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: TestRepository.java,v 1.4 2004-09-07 16:22:19 trygvis Exp $
 */
public class TestRepository
    extends AbstractRepository
{
    private String base;

    private String dir;

    public TestRepository( String scmUrl )
        throws ScmException
    {
        setDelimiter( ":" );

        setConnection( scmUrl );
    }

    public String getPassword()
    {
        throw new UnsupportedOperationException();
    }

    public void setPassword( String password )
    {
        throw new UnsupportedOperationException();
    }

    public void parseConnection()
        throws ScmException
    {
        String connection = getConnection();

        int index = connection.indexOf( ":" );

        if ( index == -1 )
        {
            throw new InvalidConnectionUrlException( "Invalid Scm Url (" + connection + " ): expected ':'." );
        }

        base = connection.substring( 0, index );

        String basedir = System.getProperty( "basedir", new File( "" ).getAbsolutePath() );

        base = new File( basedir, base.substring( base.indexOf( ":" ) + 1 ) ).getAbsolutePath();

        dir = connection.substring( index + 1 );
    }

    /**
     * @return Returns the base.
     */
    public String getBase()
    {
        return base;
    }

    /**
     * @return Returns the dir.
     */
    public String getDir()
    {
        return dir;
    }
}
