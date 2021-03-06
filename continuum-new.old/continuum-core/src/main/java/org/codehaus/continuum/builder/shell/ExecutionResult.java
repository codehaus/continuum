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

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ExecutionResult.java,v 1.2 2005-03-10 00:05:50 trygvis Exp $
 */
public class ExecutionResult
{
    private String standardOutput;

    private String standardError;

    private int exitCode;

    public ExecutionResult( String standardOutput, String standardError, int exitCode )
    {
        this.standardOutput = standardOutput;

        this.standardError = standardError;

        this.exitCode = exitCode;
    }

    public String getStandardOutput()
    {
        return standardOutput;
    }

    public String getStandardError()
    {
        return standardError;
    }

    public int getExitCode()
    {
        return exitCode;
    }
}
