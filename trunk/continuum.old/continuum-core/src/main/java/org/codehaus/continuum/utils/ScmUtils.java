package org.codehaus.continuum.utils;

/*
 * LICENSE
 */

import org.apache.maven.project.MavenProject;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.repository.RepositoryInfo;

import org.codehaus.continuum.ContinuumException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ScmUtils.java,v 1.1 2004-06-27 19:28:43 trygvis Exp $
 */
public class ScmUtils
{
    private ScmUtils()
    {
    }

    public static RepositoryInfo createRepositoryInfo( MavenProject project )
        throws ContinuumException, ScmException
    {
        RepositoryInfo info = new RepositoryInfo();

        String url = project.getScm().getDeveloperConnection();

        if ( url == null )
        {
            url = project.getScm().getConnection();

            if ( url == null )
            {
                throw new ContinuumException( "Missing scm.developerConnection and scm.connection element from the project descriptor." );
            }
        }

        info.setUrl( url );

        return info;
    }
}
