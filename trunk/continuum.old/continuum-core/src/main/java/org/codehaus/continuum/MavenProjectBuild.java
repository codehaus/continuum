package org.codehaus.plexus.continuum;

import org.apache.maven.project.Project;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: MavenProjectBuild.java,v 1.1 2004-01-16 19:38:33 jvanzyl Exp $
 */
public class MavenProjectBuild
{
    private Project project;

    private MavenProjectScm projectScm;

    public MavenProjectBuild( Project project )
    {
        this.project = project;

        projectScm = new MavenProjectScm( project );
    }

    public Project getProject()
    {
        return project;
    }

    public MavenProjectScm getProjectScm()
    {
        return projectScm;
    }
}
