package org.codehaus.continuum;

/*
 * LICENSE
 */

import org.apache.maven.project.MavenProject;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumScm.java,v 1.2 2004-05-13 17:48:17 trygvis Exp $
 */
public interface ContinuumScm
{
    String checkout( MavenProject project, String checkoutDirectory )
        throws ContinuumException;

    String update( MavenProject project, String checkoutDirectory )
        throws ContinuumException;
}
