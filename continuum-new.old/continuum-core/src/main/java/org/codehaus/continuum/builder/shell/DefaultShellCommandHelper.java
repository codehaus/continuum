package org.codehaus.continuum.builder.shell;

/*
 * Copyright 2004-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultShellCommandHelper.java,v 1.2 2005-03-10 00:05:50 trygvis Exp $
 */
public class DefaultShellCommandHelper
    extends AbstractLogEnabled
    implements ShellCommandHelper
{
    // ----------------------------------------------------------------------
    // ShellCommandHelper Implementation
    // ----------------------------------------------------------------------

    public ExecutionResult executeShellCommand( File workingDirectory, String shellCommand, String[] arguments )
        throws Exception
    {
        Commandline cl = createCommandline( workingDirectory, shellCommand, arguments );

        CommandLineUtils.StringStreamConsumer stderr = new CommandLineUtils.StringStreamConsumer();

        CommandLineUtils.StringStreamConsumer stdout = new CommandLineUtils.StringStreamConsumer();

        int exitCode;

        getLogger().info( "Executing: " + cl );

        exitCode = CommandLineUtils.executeCommandLine( cl, stdout, stderr );

        String out = stdout.getOutput();

        String err = stderr.getOutput();

        ExecutionResult result = new ExecutionResult( out, err, exitCode );

        return result;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private Commandline createCommandline( File workingDirectory, String shellCommand, String[] arguments )
    {
        Commandline cl = new Commandline();

        cl.setExecutable( shellCommand );

        cl.setWorkingDirectory( workingDirectory.getAbsolutePath() );

        for ( int i = 0; i < arguments.length; i++ )
        {
            cl.createArgument().setValue( arguments[ i ] );
        }

        getLogger().warn( "Executing external command '" + shellCommand + "'." );

        getLogger().warn( "Executing external command. Working directory: '" + workingDirectory.getAbsolutePath() + "'." );

        return cl;
    }
}
