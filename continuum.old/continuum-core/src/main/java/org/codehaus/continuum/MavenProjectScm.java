package org.codehaus.plexus.continuum;

import org.apache.maven.genericscm.command.Command;
import org.apache.maven.genericscm.command.checkout.CheckOutCommand;
import org.apache.maven.genericscm.command.tag.TagCommand;
import org.apache.maven.genericscm.command.update.UpdateCommand;
import org.apache.maven.genericscm.manager.DefaultScmManager;
import org.apache.maven.genericscm.manager.ScmManager;
import org.apache.maven.genericscm.provider.cvslib.CvsScmFactory;
import org.apache.maven.genericscm.repository.RepositoryInfo;
import org.apache.maven.project.Project;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: MavenProjectScm.java,v 1.2 2004-01-16 21:25:16 jvanzyl Exp $
 */
public class MavenProjectScm
{
    private ScmManager scmManager;

    public MavenProjectScm( Project project )
        throws Exception
    {
        RepositoryInfo repositoryInfo = new RepositoryInfo();

        repositoryInfo.setUrl( project.getRepository().getConnection() );

        scmManager = new DefaultScmManager( repositoryInfo );

        // I should not have to do this, it should be done by the factory
        // when the repo information is set.
        scmManager.addScmFactory( new CvsScmFactory() );
    }

    public void checkout( String directory )
        throws ContinuumException
    {
        try
        {
            System.out.println( "checking out sources!" );

            Command command = scmManager.getCommand( CheckOutCommand.NAME );

            command.setWorkingDirectory( directory );

            command.execute();
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
