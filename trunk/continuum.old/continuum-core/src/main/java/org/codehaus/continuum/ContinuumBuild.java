package org.codehaus.plexus.continuum;

import org.apache.maven.project.Project;
import org.apache.maven.genericscm.manager.ScmManager;
import org.apache.maven.genericscm.manager.DefaultScmManager;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: ContinuumBuild.java,v 1.1 2004-01-16 15:02:16 jvanzyl Exp $
 */
public class ContinuumBuild
{
    private Project project;

    private ScmManager scmManager;

    public ContinuumBuild( Project project )
    {
        this.project = project;

        scmManager = new DefaultScmManager( new MavenScmInfoAdapter( project ) );
    }

    public Project getProject()
    {
        return project;
    }

    public ScmManager getScmManager()
    {
        return scmManager;
    }
}
