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

import com.thoughtworks.xstream.XStream;

import org.apache.maven.ExecutionResponse;
import org.apache.maven.plugin.FailureResponse;

import org.codehaus.continuum.project.AbstractContinuumBuildResult;
import org.codehaus.continuum.project.ContinuumBuild;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: Maven2BuildResult.java,v 1.1 2004-08-29 18:45:15 trygvis Exp $
 */
public class Maven2BuildResult
    extends AbstractContinuumBuildResult
{
    private transient XStream xStream = new XStream();

    private boolean success;

    private String longMessage;

    private String shortMessage;

    private String executionResponse; /*
    private ExecutionResponse executionResponse; /* */

    private Maven2BuildResult()
    {
    }

    public Maven2BuildResult( ContinuumBuild build, ExecutionResponse executionResponse )
    {
        super( build );

        this.executionResponse = xStream.toXML( executionResponse );

        this.success = !executionResponse.isExecutionFailure();

        FailureResponse failureResponse = executionResponse.getFailureResponse();

        if ( failureResponse != null )
        {
            longMessage = failureResponse.longMessage();

            shortMessage = failureResponse.shortMessage();
        }
    }

    public boolean isSuccess()
    {
        return success;
    }

    private void setSuccess( boolean success )
    {
        this.success = success;
    }
/*
    public ExecutionResponse getExecutionResponse()
    {
        return executionResponse;
    }

    private void setExecutionResponse( ExecutionResponse executionResponse )
    {
        this.executionResponse = executionResponse;
    }
*/

    public String getExecutionResponse()
    {
        return executionResponse;
    }

    private void setExecutionResponse( String executionResponse )
    {
        this.executionResponse = executionResponse;
    }

    public String getShortFailureResponse()
    {
        return shortMessage;
    }

    public String getLongFailureResponse()
    {
        return longMessage;
    }
}
