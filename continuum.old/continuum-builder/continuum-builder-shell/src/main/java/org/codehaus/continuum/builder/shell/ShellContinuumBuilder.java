package org.codehaus.continuum.builder.shell;

/*
 * Copyright (c) 2004, Jason van Zyl and Trygve Laugst�l
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

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.builder.AbstractContinuumBuilder;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumBuildResult;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ShellContinuumBuilder.java,v 1.4 2004-10-29 14:48:04 trygvis Exp $
 */
public abstract class ShellContinuumBuilder
    extends AbstractContinuumBuilder
{
    // ----------------------------------------------------------------------
    // Components
    // ----------------------------------------------------------------------

    // ----------------------------------------------------------------------
    // Configuration
    // ----------------------------------------------------------------------

    protected String shellCommand;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    protected abstract Commandline createCommandline( File workingDirectory, ShellProjectDescriptor projectDescriptor );    

    // ----------------------------------------------------------------------
    // ContinuumBuilder implementation
    // ----------------------------------------------------------------------

    public synchronized ContinuumBuildResult build( File workingDirectory, ContinuumBuild build )
        throws ContinuumException
    {
        ShellProjectDescriptor projectDescriptor = (ShellProjectDescriptor) build.getProject().getDescriptor();

        Commandline cl = createCommandline( workingDirectory, projectDescriptor );

        CommandLineUtils.StringStreamConsumer stderr = new CommandLineUtils.StringStreamConsumer();

        CommandLineUtils.StringStreamConsumer stdout = new CommandLineUtils.StringStreamConsumer();

        int exitCode;

        getLogger().info( "executing: " + cl );

        try
        {
            exitCode = CommandLineUtils.executeCommandLine( cl, stdout, stderr );
        }
        catch( Exception ex )
        {
            throw new ContinuumException( "Error while executing command.", ex );
        }

        String out = stdout.getOutput();

        String err = stderr.getOutput();

        boolean success = exitCode == 0;

        if ( build != null )
        {
            ShellBuildResult result = new ShellBuildResult( build, success, out, err, exitCode );

            return result;
        }
        else
        {
            return null;
        }
    }
}
