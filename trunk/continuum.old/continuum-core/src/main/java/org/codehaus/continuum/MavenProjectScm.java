package org.codehaus.plexus.continuum;

import org.apache.maven.genericscm.manager.DefaultScmManager;
import org.apache.maven.genericscm.manager.ScmManager;
import org.apache.maven.project.Project;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: MavenProjectScm.java,v 1.3 2004-01-17 23:16:51 jvanzyl Exp $
 */
public class MavenProjectScm
{
    private ScmManager scmManager;

    public MavenProjectScm( Project project )
        throws Exception
    {
        scmManager = new DefaultScmManager( project.getRepository().getConnection() );
    }

    public void checkout( String directory )
        throws ContinuumException
    {
        try
        {
            scmManager.checkout( directory );
        }
        catch ( Exception e )
        {
            e.printStackTrace();

            throw new ContinuumException( "Cannot checkout sources: ", e );
        }
    }

    public void update( String directory )
        throws ContinuumException
    {
        try
        {
            scmManager.update( directory );
        }
        catch ( Exception e )
        {
            throw new ContinuumException( "Cannot checkout sources: ", e );
        }
    }
}
