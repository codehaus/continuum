package org.codehaus.continuum.project;

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


/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AbstractContinuumBuild.java,v 1.2 2004-07-27 05:42:10 trygvis Exp $
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
