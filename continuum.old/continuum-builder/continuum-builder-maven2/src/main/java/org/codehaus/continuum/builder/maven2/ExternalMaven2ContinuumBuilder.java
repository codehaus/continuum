package org.codehaus.continuum.builder.maven2;

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

import java.io.File;
import java.util.List;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.builder.ContinuumBuilder;
import org.codehaus.continuum.maven.ExternalMavenExecutionResult;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumBuildResult;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ExternalMaven2ContinuumBuilder.java,v 1.7 2004-10-28 21:23:30 trygvis Exp $
 */
public class ExternalMaven2ContinuumBuilder
    extends Maven2ContinuumBuilder
    implements ContinuumBuilder, Initializable
{
    // ----------------------------------------------------------------------
    // ContinuumBuilder implementation
    // ----------------------------------------------------------------------

    public synchronized ContinuumBuildResult build( File workingDirectory, ContinuumBuild build )
        throws ContinuumException
    {
        Maven2ProjectDescriptor descriptor;

        descriptor = (Maven2ProjectDescriptor) build.getProject().getDescriptor();

        List goals = descriptor.getGoals();

        // ----------------------------------------------------------------------
        // Build it
        // ----------------------------------------------------------------------

        return execute( workingDirectory, build, goals );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    protected Maven2BuildResult execute( File workingDirectory, ContinuumBuild build, List goals )
        throws ContinuumException
    {
        ExternalMavenExecutionResult externalResult;

        externalResult = getMavenTool().executeExternal( workingDirectory, goals );

        int exitCode = externalResult.getExitCode();

        String out = externalResult.getSystemOut();

        String err = externalResult.getSystemErr();

        boolean success = out.indexOf( "BUILD SUCCESSFUL" ) != -1;

        if ( build != null )
        {
            ExternalMaven2BuildResult result = new ExternalMaven2BuildResult( build, success, out, err, exitCode );

            return result;
        }
        else
        {
            return null;
        }
    }
}
