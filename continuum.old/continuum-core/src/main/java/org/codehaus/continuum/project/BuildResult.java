package org.codehaus.continuum.project;

/*
 * LICENSE
 */

import java.util.Date;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: BuildResult.java,v 1.3 2004-07-03 03:21:15 trygvis Exp $
 */
public class BuildResult
{
    /** */
    public final static int BUILD_BUILDING = 1;

    /** */
    public final static int BUILD_RESULT_OK = 2;

    /** */
    public final static int BUILD_RESULT_FAILURE = 3;

    /** */
    public final static int BUILD_RESULT_ERROR = 4;

    /** */
    private String buildId;

    /** */
    private String projectId;

    /** */
    private int state;

    /** */
    private Date startTime;

    /** */
    private Date endTime;

    /** */
    private Throwable error;

    /**
     */
    public BuildResult( String buildId )
    {
        this.buildId = buildId;
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
     * @return Returns the project id.
     */
    public String getProjectId()
    {
        return projectId;
    }

    /**
     * @return Returns the state.
     */
    public int getState()
    {
        return state;
    }

    /**
     * @param state The state to set.
     */
    public void setState( int state )
    {
        this.state = state;
    }

    /**
     * @param projectId The project id to set.
     */
    public void setProjectId( String projectId )
    {
        this.projectId = projectId;
    }

    /**
     * @return Returns the start time.
     */
    public Date getStartTime()
    {
        return startTime;
    }

    /**
     * @param startTime The start time to set.
     */
    public void setStartTime( Date startTime )
    {
        this.startTime = startTime;
    }

    /**
     * @return Returns the end time.
     */
    public Date getEndTime()
    {
        return endTime;
    }

    /**
     * @param endTime The end time to set.
     */
    public void setEndTime( Date endTime )
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
    public void setError(Throwable error)
    {
        this.error = error;
    }

    public static String buildStateToString( int state )
    {
        switch( state )
        {
        case BUILD_BUILDING:
            return "building";
        case BUILD_RESULT_OK:
            return "ok";
        case BUILD_RESULT_FAILURE:
            return "failure";
        case BUILD_RESULT_ERROR:
            return "error";
        default:
            return "UNKNOWN";
        }
    }
}
