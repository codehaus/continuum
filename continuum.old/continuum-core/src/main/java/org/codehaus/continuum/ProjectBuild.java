package org.codehaus.plexus.continuum;

import org.apache.maven.project.Project;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: ProjectBuild.java,v 1.1 2004-01-16 19:36:50 jvanzyl Exp $
 */
public class ProjectBuild
{
    private Project project;

    private ProjectScm projectScm;

    public ProjectBuild( Project project )
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
