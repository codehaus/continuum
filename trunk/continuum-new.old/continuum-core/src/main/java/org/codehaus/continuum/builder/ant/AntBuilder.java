package org.codehaus.continuum.builder.ant;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
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

import java.io.File;
import java.net.URL;
import java.util.Properties;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.builder.ContinuumBuilder;
import org.codehaus.continuum.builder.shell.ExecutionResult;
import org.codehaus.continuum.builder.shell.ShellCommandHelper;
import org.codehaus.continuum.project.ContinuumBuildResult;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AntBuilder.java,v 1.1 2005-03-09 00:14:39 trygvis Exp $
 */
public class AntBuilder
    extends AbstractLogEnabled
    implements ContinuumBuilder
{
    public static final String CONFIGURATION_EXECUTABLE = "executable";

    public static final String CONFIGURATION_TARGETS = "targets";

    /** @requirement */
    private ShellCommandHelper shellCommandHelper;

    // ----------------------------------------------------------------------
    // ContinuumBuilder Implementation
    // ----------------------------------------------------------------------

    public ContinuumBuildResult build( File workingDirectory, ContinuumProject project )
        throws ContinuumException
    {
        Properties configuration = project.getConfiguration();

        String executable = configuration.getProperty( CONFIGURATION_EXECUTABLE );

        String[] goals = StringUtils.split( configuration.getProperty( CONFIGURATION_TARGETS ), "," );

        ExecutionResult executionResult;

        try
        {
            executionResult = shellCommandHelper.executeShellCommand( workingDirectory, executable, goals );
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
        return null;
    }

    public void updateProjectFromCheckOut( File workingDirectory, ContinuumProject project )
        throws ContinuumException
    {
        // Not much to do.
    }
}
