package org.codehaus.continuum.test.scm;

/*
 * Copyright (c) 2004, Jason van Zyl and Trygve Laugstøl
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
 * @version $Id: TestScm.java,v 1.3 2004-07-29 04:27:42 trygvis Exp $
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
