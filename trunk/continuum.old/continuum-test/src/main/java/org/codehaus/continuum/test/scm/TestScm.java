package org.codehaus.continuum.test.scm;

/*
 * LICENSE
 */

import java.util.HashMap;
import java.util.Map;

import org.apache.maven.scm.Scm;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.command.Command;
import org.apache.maven.scm.command.CommandWrapper;
import org.apache.maven.scm.command.checkout.CheckOutCommand;
import org.apache.maven.scm.command.update.UpdateCommand;
import org.apache.maven.scm.repository.Repository;
import org.apache.maven.scm.repository.RepositoryInfo;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: TestScm.java,v 1.2 2004-07-27 00:06:11 trygvis Exp $
 */
public class TestScm
    implements Scm
{
    private Map commands = new HashMap();

    public TestScm()
    {
        commands.put( CheckOutCommand.NAME, TestCheckOutCommand.class );
        commands.put( UpdateCommand.NAME, TestUpdateCommand.class );
    }

    public String getSupportedScm()
    {
        return "test";
    }

    public CommandWrapper createCommandWrapper( RepositoryInfo repoInfo )
        throws ScmException
    {
        throw new ScmException( "Not implemented" );
    }

    public Repository createRepository( RepositoryInfo repoInfo )
        throws ScmException
    {
        throw new ScmException( "Not implemented" );
    }

    public Command createCommand( RepositoryInfo repoInfo, String commandName )
        throws ScmException
    {
        Class clazz = (Class) commands.get( commandName );

        if ( clazz == null )
        {
            throw new ScmException( "Unknown command: '" + commandName + "'." );
        }

        Command command;

        try
        {
            command = (Command) clazz.newInstance();
        }
        catch( Exception ex )
        {
            throw new ScmException( "Exception while instanciating command." );
        }

        command.setRepository( new TestRepository( repoInfo.getConnection() ) );

        return command;
    }
}
