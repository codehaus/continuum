package org.codehaus.plexus.continuum;

import org.apache.maven.project.Project;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: ContinuumBuild.java,v 1.2 2004-01-16 17:57:17 jvanzyl Exp $
 */
public class ContinuumBuild
{
    private Project project;

    private ProjectScm projectScm;

    public ContinuumBuild( Project project )
    {
        this.project = project;

        projectScm = new ProjectScm( project );
    }

    public Project getProject()
    {
        return project;
    }

    public ProjectScm getProjectScm()
    {
        return projectScm;
    }
}
