package org.codehaus.continuum.utils;

/*
 * LICENSE
 */

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.repository.RepositoryInfo;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.project.ContinuumProject;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ScmUtils.java,v 1.2 2004-07-03 03:21:16 trygvis Exp $
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
