package org.codehaus.plexus.continuum;

import org.apache.maven.project.MavenProject;
import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.scm.manager.DefaultScmManager;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: MavenProjectScm.java,v 1.4 2004-02-02 20:15:59 jvanzyl Exp $
 */
public class MavenProjectScm
{
    private ScmManager scmManager;

    public MavenProjectScm( MavenProject project )
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
