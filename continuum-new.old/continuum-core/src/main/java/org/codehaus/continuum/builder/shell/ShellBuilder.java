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

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.builder.AbstractContinuumBuilder;
import org.codehaus.continuum.project.ContinuumBuildResult;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ShellBuilder.java,v 1.8 2005-03-28 12:11:43 trygvis Exp $
 */
public abstract class ShellBuilder
    extends AbstractContinuumBuilder
{
    /** @requirement */
    private ShellCommandHelper shellCommandHelper;

    /** @configuration */
    private String shellCommand;

    /** @configuration */
    private String[] arguments;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    protected String[] getArguments( ContinuumProject project )
        throws ContinuumException
    {
        if ( arguments == null )
        {
            arguments = new String[ 0 ];
        }

        return arguments;
    }

    // ----------------------------------------------------------------------
    // ContinuumBuilder implementation
    // ----------------------------------------------------------------------

    public synchronized ContinuumBuildResult build( ContinuumProject project )
        throws ContinuumException
    {
        File workingDirectory = new File( project.getWorkingDirectory() );

        ExecutionResult executionResult;

        String[] arguments = getArguments( project );

        try
        {
            executionResult = shellCommandHelper.executeShellCommand( workingDirectory, shellCommand, arguments );
        }
        catch ( Exception e )
        {
            throw new ContinuumException( "Error while executing shell command.", e );
        }

        boolean success = executionResult.getExitCode() == 0;

        ShellBuildResult result = new ShellBuildResult();

        result.setSuccess( success );

        result.setStandardOutput( executionResult.getStandardOutput() );

        result.setStandardError( executionResult.getStandardError() );

        result.setExitCode( executionResult.getExitCode() );

        return result;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    protected Commandline createCommandline( ContinuumProject project, String executable, String[] arguments )
    {
        Commandline cl = new Commandline();

        cl.setExecutable( executable );

        cl.setWorkingDirectory( new File( project.getWorkingDirectory() ).getAbsolutePath() );

        for ( int i = 1; i < arguments.length; i++ )
        {
            cl.createArgument().setValue( arguments[i] );
        }

        getLogger().warn( "Executing external command '" + executable + "'." );

        getLogger().warn( "Executing external command. Working directory: " + cl.getWorkingDirectory().getAbsolutePath() );

        return cl;
    }
}
