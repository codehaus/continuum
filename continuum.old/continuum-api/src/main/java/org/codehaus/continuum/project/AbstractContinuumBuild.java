package org.codehaus.continuum.project;


/*
 * LICENSE
 */

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AbstractContinuumBuild.java,v 1.1 2004-07-27 00:06:02 trygvis Exp $
 */
public abstract class AbstractContinuumBuild
    implements ContinuumBuild
{
    /** */
    private String id;

    /** */
    private ContinuumProject project;

    /** */
    private ContinuumProjectState state;

    /** */
    private long startTime;

    /** */
    private long endTime;

    /** */
    private Throwable error;

    private ContinuumBuildResult buildResult;

    /**
     */
    public AbstractContinuumBuild()
    {
    }

    /**
     * @return Returns the id.
     */
    public String getId()
    {
        return id;
    }

    /**
     * @param id The id to set.
     */
    public void setId( String id )
    {
        this.id = id;
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
    public ContinuumProjectState getState()
    {
        return state;
    }

    /**
     * @param state The state to set.
     */
    public void setState( ContinuumProjectState state )
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

    public ContinuumBuildResult getBuildResult()
    {
        return buildResult;
    }

    public void setBuildResult( ContinuumBuildResult buildResult )
    {
        this.buildResult = buildResult;
    }

    public static String buildStateToString( ContinuumProjectState state )
    {
        if( state == ContinuumProjectState.BUILDING )
        {
            return "building";
        }
        else if ( state == ContinuumProjectState.OK )
        {
            return "ok";
        }
        else if ( state == ContinuumProjectState.FAILED )
        {
            return "failed";
        }
        else if ( state == ContinuumProjectState.ERROR )
        {
            return "error";
        }
        else
        {
            return "UNKNOWN";
        }
    }
}
