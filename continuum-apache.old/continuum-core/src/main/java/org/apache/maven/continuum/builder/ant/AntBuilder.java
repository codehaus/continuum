package org.apache.maven.continuum.builder.ant;

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
import java.net.URL;
import java.util.Properties;

import org.apache.maven.continuum.ContinuumException;
import org.apache.maven.continuum.builder.AbstractContinuumBuilder;
import org.apache.maven.continuum.builder.ContinuumBuilder;
import org.apache.maven.continuum.builder.shell.ExecutionResult;
import org.apache.maven.continuum.builder.shell.ShellCommandHelper;
import org.apache.maven.continuum.project.ContinuumBuildResult;
import org.apache.maven.continuum.project.ContinuumProject;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AntBuilder.java,v 1.1.1.1 2005-03-29 20:42:00 trygvis Exp $
 */
public class AntBuilder
    extends AbstractContinuumBuilder
    implements ContinuumBuilder
{
    public static final String CONFIGURATION_EXECUTABLE = "executable";

    public static final String CONFIGURATION_TARGETS = "targets";

    /** @requirement */
    private ShellCommandHelper shellCommandHelper;

    // ----------------------------------------------------------------------
    // ContinuumBuilder Implementation
    // ----------------------------------------------------------------------

    public ContinuumBuildResult build( ContinuumProject project )
        throws ContinuumException
    {
        Properties configuration = project.getConfiguration();

        File workingDirectory = new File( project.getWorkingDirectory() );

        String executable = getConfigurationString( configuration, CONFIGURATION_EXECUTABLE );

        String[] targets = getConfigurationStringArray( configuration, CONFIGURATION_TARGETS, "," );

        ExecutionResult executionResult;

        try
        {
            executionResult = shellCommandHelper.executeShellCommand( workingDirectory, executable, targets );
        }
        catch ( Exception e )
        {
            throw new ContinuumException( "Error while executing shell command.", e );
        }

        boolean success = executionResult.getExitCode() == 0;

        AntBuildResult result = new AntBuildResult();

        result.setSuccess( success );

        result.setStandardOutput( executionResult.getStandardOutput() );

        result.setStandardError( executionResult.getStandardError() );

        result.setExitCode( executionResult.getExitCode() );

        return result;
    }

    public ContinuumProject createProjectFromMetadata( URL metadata )
        throws ContinuumException
    {
        throw new ContinuumException( "The Ant builder cannot create metadata from a URL." );
    }

    public void updateProjectFromCheckOut( File workingDirectory, ContinuumProject project )
        throws ContinuumException
    {
        // Not much to do.
    }
}
