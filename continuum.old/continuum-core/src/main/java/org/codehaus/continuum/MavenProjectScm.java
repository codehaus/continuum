package org.codehaus.plexus.continuum;

import org.apache.maven.project.Project;
import org.apache.maven.genericscm.manager.DefaultScmManager;
import org.apache.maven.genericscm.manager.ScmManager;
import org.apache.maven.genericscm.command.Command;
import org.apache.maven.genericscm.command.tag.TagCommand;
import org.apache.maven.genericscm.command.update.UpdateCommand;
import org.apache.maven.genericscm.command.checkout.CheckOutCommand;
import org.apache.maven.genericscm.ScmException;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: MavenProjectScm.java,v 1.1 2004-01-16 19:38:33 jvanzyl Exp $
 */
public class MavenProjectScm
{
    private ScmManager scmManager;

    public MavenProjectScm( Project project )
    {
        scmManager = new DefaultScmManager( new MavenScmInfoAdapter( project ) );
    }

    public void checkout( String directory )
        throws ContinuumException
    {
        try
        {
            Command command = scmManager.getCommand( CheckOutCommand.NAME );

            command.setWorkingDirectory( directory );

            command.execute();
        }
        catch ( Exception e )
        {
            throw new ContinuumException( "Cannot checkout sources: ", e );
        }
    }

    public void update( String directory )
        throws ContinuumException
    {
        try
        {
            Command command = scmManager.getCommand( UpdateCommand.NAME );

            command.setWorkingDirectory( directory );

            command.execute();
        }
        catch ( Exception e )
        {
            throw new ContinuumException( "Cannot checkout sources: ", e );
        }
    }

    // This is not supported by SCM yet.
    public void tag( String tagId )
        throws ContinuumException
    {
        try
        {
            Command command = scmManager.getCommand( TagCommand.NAME );

            command.execute();
        }
        catch ( Exception e )
        {
            throw new ContinuumException( "Cannot checkout sources: ", e );
        }
    }

}
