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
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ShellBuilder.java,v 1.6 2005-03-10 00:05:50 trygvis Exp $
 */
public abstract class ShellBuilder
    extends AbstractContinuumBuilder
{
    /** @requirement */
    private ShellCommandHelper shellCommandHelper;

    /** @configuration */
    private String shellCommand;

    // ----------------------------------------------------------------------
    // ContinuumBuilder implementation
    // ----------------------------------------------------------------------

    public synchronized ContinuumBuildResult build( ContinuumProject project )
        throws ContinuumException
    {
        File workingDirectory = new File( project.getWorkingDirectory() );

        ExecutionResult executionResult;

        try
        {
            executionResult = shellCommandHelper.executeShellCommand( workingDirectory, shellCommand, new String[ 0 ] );
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

    protected Commandline createCommandline( File workingDirectory, ContinuumProject project )
    {
        Commandline cl = new Commandline();

        String[] s = StringUtils.split( shellCommand );

        cl.setExecutable( s[0] );

        cl.setWorkingDirectory( workingDirectory.getAbsolutePath() );

        for ( int i = 1; i < s.length; i++ )
        {
            cl.createArgument().setValue( s[i] );
        }

        getLogger().warn( "Executing external command '" + shellCommand + "'." );

        getLogger().warn( "Executing external command. Working directory: " + cl.getWorkingDirectory().getAbsolutePath() );

        return cl;
    }
}
