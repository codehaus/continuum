package org.codehaus.continuum.project;

/*
 * LICENSE
 */

import java.io.Serializable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: BuildResult.java,v 1.1 2004-07-20 18:25:57 trygvis Exp $
 */
public interface BuildResult
    extends Serializable
{
    /**
     * @return Returns the buildId.
     */
    public String getBuildId();

    /**
     * @param buildId The buildId to set.
     */
    public void setBuildId( String buildId );

    /**
     * @return Returns the project.
     */
    public ContinuumProject getProject();

    /**
     * @param project The project to set.
     */
    public void setProject( ContinuumProject project );

    /**
     * @return Returns the state.
     */
    public BuildResultState getState();

    /**
     * @param state The state to set.
     */
    public void setState( BuildResultState state );

    /**
     * @return Returns the start time.
     */
    public long getStartTime();

    /**
     * @param startTime The start time to set.
     */
    public void setStartTime( long startTime );

    /**
     * @return Returns the end time.
     */
    public long getEndTime();

    /**
     * @param endTime The end time to set.
     */
    public void setEndTime( long endTime );

    /**
     * @return Returns the error.
     */
    public Throwable getError();

    /**
     * @param error The error to set.
     */
    public void setError( Throwable Error );
/*
    public ExecutionResponse getMaven2Result();

    public void setMaven2Result( ExecutionResponse executionResponse );
*/
    public Object getBuilderBuildResult();

    public void setBuilderBuildResult( Object result );
}
