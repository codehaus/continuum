package org.codehaus.plexus.continuum;

/*
 * LICENSE
 */

import org.apache.maven.project.MavenProject;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumScm.java,v 1.1 2004-04-24 23:54:12 trygvis Exp $
 */
public interface ContinuumScm
{
    String checkout( MavenProject project, String checkoutDirectory )
        throws ContinuumException;

    String update( MavenProject project, String checkoutDirectory )
        throws ContinuumException;
}
