package org.codehaus.continuum.notification.mail;

/*
 * LICENSE
 */

import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumProject;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: MailGenerator.java,v 1.1 2004-10-06 14:09:44 trygvis Exp $
 */
public interface MailGenerator
{
    public String generateContent( ContinuumProject project, ContinuumBuild build, ContinuumBuild lastBuild );

    public String generateSubject( ContinuumProject project, ContinuumBuild build, ContinuumBuild lastBuild );
}
