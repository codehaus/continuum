package org.codehaus.continuum;

import org.apache.maven.project.MavenProject;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: MavenProjectBuild.java,v 1.4 2004-05-13 17:48:17 trygvis Exp $
 */
public class MavenProjectBuild
{
    private MavenProject project;

    private MavenProjectScm projectScm;

    public MavenProjectBuild( MavenProject project )
        throws Exception
    {
        this.project = project;

        projectScm = new MavenProjectScm( project );
    }

    public MavenProject getProject()
    {
        return project;
    }

    public MavenProjectScm getProjectScm()
    {
        return projectScm;
    }
}
