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

import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.AbstractContinuumBuildResult;

public class ShellBuildResult
    extends AbstractContinuumBuildResult
{
    private boolean success;

    private String standardOutput;

    private String standardError;

    private int exitCode;

    protected ShellBuildResult()
    {
    }

    public ShellBuildResult( ContinuumBuild build, boolean success, String standardOutput, String standardError, int exitCode )
    {
        super( build );

        this.success = success;

        this.standardOutput = standardOutput;

        this.standardError = standardError;

        this.exitCode = exitCode;
    }

    public void setSuccess( boolean success )
    {
        this.success = success;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public int getExitCode()
    {
        return exitCode;
    }

    public void setExitCode( int exitCode )
    {
        this.exitCode = exitCode;
    }

    public String getStandardOutput()
    {
        return standardOutput;
    }

    public void setStandardOutput( String standardOutput )
    {
        this.standardOutput = standardOutput;
    }

    public String getStandardError()
    {
        return standardError;
    }

    public void setStandardError( String standardError )
    {
        this.standardError = standardError;
    }
}
