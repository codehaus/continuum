package org.codehaus.plexus.continuum.notification;

import org.apache.maven.project.MavenProject;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: ContinuumNotifier.java,v 1.1 2004-02-02 20:36:18 jvanzyl Exp $
 */
public interface ContinuumNotifier
{
    static String ROLE = ContinuumNotifier.class.getName();

    void notifyAudience( MavenProject project, String message )
        throws Exception;
}
