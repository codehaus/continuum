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

import org.codehaus.continuum.project.ContinuumBuild;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ExternalMaven2BuildResult.java,v 1.3 2004-10-14 13:59:59 trygvis Exp $
 */
public class ExternalMaven2BuildResult
    extends Maven2BuildResult
{
    private String standardOutput;

    private String standardError;

    private int exitCode;

    protected ExternalMaven2BuildResult()
    {
    }

    public ExternalMaven2BuildResult( ContinuumBuild build, boolean success, String standardOutput, String standardError, int exitCode )
    {
        super( build, success );

        this.standardOutput = standardOutput;

        this.standardError = standardError;

        this.exitCode = exitCode;
    }

    /**
     * @return Returns the exitCode.
     */
    public int getExitCode()
    {
        return exitCode;
    }

    /**
     * @param exitCode The exitCode to set.
     */
    public void setExitCode( int exitCode )
    {
        this.exitCode = exitCode;
    }

    /**
     * @return Returns the standardOutput.
     */
    public String getStandardOutput()
    {
        return standardOutput;
    }

    /**
     * @param standardOutput The standardOutput to set.
     */
    public void setStandardOutput( String standardOutput )
    {
        this.standardOutput = standardOutput;
    }

    /**
     * @return Returns the standardError.
     */
    public String getStandardError()
    {
        return standardError;
    }

    /**
     * @param standardError The standardError to set.
     */
    public void setStandardError( String standardError )
    {
        this.standardError = standardError;
    }
}
