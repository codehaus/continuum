package org.codehaus.continuum.maven;

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

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ExternalMavenExecutionResult.java,v 1.1.1.1 2004-10-28 16:08:17 trygvis Exp $
 */
public class ExternalMavenExecutionResult
{
    private String systemOut;

    private String systemErr;

    public ExternalMavenExecutionResult( String systemOut, String systemErr, int exitCode )
    {
        this.systemOut = systemOut;

        this.systemErr = systemErr;

        this.exitCode = exitCode;
    }

    private int exitCode;

    public String getSystemOut()
    {
        return systemOut;
    }

    public String getSystemErr()
    {
        return systemErr;
    }

    public int getExitCode()
    {
        return exitCode;
    }
}
