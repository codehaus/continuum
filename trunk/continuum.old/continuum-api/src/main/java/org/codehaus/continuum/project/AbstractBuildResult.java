package org.codehaus.continuum.project;


/*
 * LICENSE
 */

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AbstractBuildResult.java,v 1.1 2004-07-20 18:25:57 trygvis Exp $
 */
public abstract class AbstractBuildResult
    implements BuildResult
{
    /** */
    private String buildId;

    /** */
    private ContinuumProject project;

    /** */
    private BuildResultState state;

    /** */
    private long startTime;

    /** */
    private long endTime;

    /** */
    private Throwable error;

    /**
     * TODO: Refactor to a new Maven2BuildResult class.
     */
//    private ExecutionResponse maven2Result;
    private Object buildResult;

    /**
     */
    public AbstractBuildResult()
    {
    }

    /**
     * @return Returns the buildId.
     */
    public String getBuildId()
    {
        return buildId;
    }

    /**
     * @param buildId The buildId to set.
     */
    public void setBuildId( String buildId )
    {
        this.buildId = buildId;
    }

    /**
     * @return Returns the project.
     */
    public ContinuumProject getProject()
    {
        return project;
    }

    /**
     * @return Returns the state.
     */
    public BuildResultState getState()
    {
        return state;
    }

    /**
     * @param state The state to set.
     */
    public void setState( BuildResultState state )
    {
        this.state = state;
    }

    /**
     * @param project The project to set.
     */
    public void setProject( ContinuumProject project )
    {
        this.project = project;
    }

    /**
     * @return Returns the start time.
     */
    public long getStartTime()
    {
        return startTime;
    }

    /**
     * @param startTime The start time to set.
     */
    public void setStartTime( long startTime )
    {
        this.startTime = startTime;
    }

    /**
     * @return Returns the end time.
     */
    public long getEndTime()
    {
        return endTime;
    }

    /**
     * @param endTime The end time to set.
     */
    public void setEndTime( long endTime )
    {
        this.endTime = endTime;
    }

    /**
     * @return Returns the error.
     */
    public Throwable getError()
    {
        return error;
    }

    /**
     * @param error The error to set.
     */
    public void setError( Throwable error )
    {
        this.error = error;
    }
/*
    public ExecutionResponse getMaven2Result()
    {
        return maven2Result;
    }

    public void setMaven2Result( ExecutionResponse executionResponse )
    {
        this.maven2Result = executionResponse;
    }
*/
    public Object getBuilderBuildResult()
    {
        return buildResult;
    }

    public void setBuilderBuildResult( Object buildResult )
    {
        this.buildResult = buildResult;
    }

    public static String buildStateToString( BuildResultState state )
    {
        if( state == BuildResultState.BUILDING )
        {
            return "building";
        }
        else if ( state == BuildResultState.RESULT_OK )
        {
            return "ok";
        }
        else if ( state == BuildResultState.RESULT_FAILURE )
        {
            return "failure";
        }
        else if ( state == BuildResultState.RESULT_ERROR )
        {
            return "error";
        }
        else
        {
            return "UNKNOWN";
        }
    }
}
