package org.codehaus.continuum;

/*
 * LICENSE
 */

import org.apache.maven.project.MavenProject;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumScm.java,v 1.3 2004-06-27 19:28:43 trygvis Exp $
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
