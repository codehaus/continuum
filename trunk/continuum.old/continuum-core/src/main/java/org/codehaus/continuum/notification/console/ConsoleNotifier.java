package org.codehaus.plexus.continuum.notification.console;

/*
 * LISENCE
 */

import org.apache.maven.project.MavenProject;

import org.codehaus.plexus.continuum.notification.ContinuumNotifier;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ConsoleNotifier.java,v 1.1 2004-04-07 15:56:56 trygvis Exp $
 */
public class ConsoleNotifier
    implements ContinuumNotifier
{
    public void notifyAudience(MavenProject project, String message)
        throws Exception
    {
        System.out.println( "Build of " + project.getName() + " complete!" );
        System.out.println( "Message: " + message );
    }
}
