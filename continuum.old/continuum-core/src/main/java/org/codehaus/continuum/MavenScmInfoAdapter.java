package org.codehaus.plexus.continuum;

import org.apache.maven.project.Project;
import org.apache.maven.genericscm.repository.RepositoryInfo;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: MavenScmInfoAdapter.java,v 1.1 2004-01-16 15:02:16 jvanzyl Exp $
 */
public class MavenScmInfoAdapter
    extends RepositoryInfo
{
    private Project project;

    public MavenScmInfoAdapter( Project project )
    {
        this.project = project;
    }
}
