package org.codehaus.continuum.scm;

/*
 * LICENSE
 */

import org.apache.maven.project.MavenProject;

import org.codehaus.continuum.ContinuumException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumScm.java,v 1.5 2004-07-01 15:30:58 trygvis Exp $
 */
public interface ContinuumScm
{
    String ROLE = ContinuumScm.class.getName();

    void clean( MavenProject project )
        throws ContinuumException;

    String checkout( MavenProject project )
        throws ContinuumException;

    String update( MavenProject project )
        throws ContinuumException;
}
