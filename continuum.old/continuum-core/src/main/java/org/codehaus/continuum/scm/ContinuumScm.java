package org.codehaus.continuum.scm;

/*
 * LICENSE
 */

import org.apache.maven.project.MavenProject;

import org.codehaus.continuum.ContinuumException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumScm.java,v 1.4 2004-06-27 23:21:03 trygvis Exp $
 */
public interface ContinuumScm
{
    void clean( MavenProject project )
        throws ContinuumException;

    String checkout( MavenProject project )
        throws ContinuumException;

    String update( MavenProject project )
        throws ContinuumException;
}
