package org.codehaus.continuum.test.scm;

/*
 * LICENSE
 */

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.repository.AbstractRepository;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: TestRepository.java,v 1.1 2004-07-27 00:06:11 trygvis Exp $
 */
public class TestRepository
    extends AbstractRepository
{
    private String base;

    private String dir;

    private boolean valid;

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
    {
        String connection = getConnection();

        int index = connection.indexOf( ":" );

        if ( index == -1 )
        {
            valid = false;

            return;
        }

        base = connection.substring( 0, index );

        base = base.substring( base.indexOf( ":" ) + 1 );

        dir = connection.substring( base.length() + 1 );

        valid = true;
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

    public boolean isValid()
    {
        return valid;
    }
}
