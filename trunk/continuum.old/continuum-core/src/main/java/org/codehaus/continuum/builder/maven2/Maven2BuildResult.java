package org.codehaus.continuum.builder.maven2;

/*
 * LICENSE
 */

import com.thoughtworks.xstream.XStream;

import org.apache.maven.ExecutionResponse;
import org.apache.maven.plugin.FailureResponse;

import org.codehaus.continuum.project.AbstractContinuumBuildResult;
import org.codehaus.continuum.project.ContinuumBuild;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: Maven2BuildResult.java,v 1.1 2004-07-27 00:06:04 trygvis Exp $
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
