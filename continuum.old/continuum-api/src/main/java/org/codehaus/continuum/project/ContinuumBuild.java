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

import java.io.Serializable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumBuild.java,v 1.3 2004-10-09 13:00:17 trygvis Exp $
 */
public interface ContinuumBuild
    extends Serializable
{
    /**
     * @return Returns the buildId.
     */
    public String getId();

    /**
     * @param buildId The buildId to set.
     */
//    public void setBuildId( String buildId );

    /**
     * @return Returns the project.
     */
    public ContinuumProject getProject();

    /**
     * @param project The project to set.
     */
//    public void setProject( ContinuumProject project );

    /**
     * @return Returns the state.
     */
    public ContinuumProjectState getState();

    /**
     * @param state The state to set.
     */
//    public void setState( ContinuumProjectState state );

    /**
     * @return Returns the start time.
     */
    public long getStartTime();

    /**
     * @param startTime The start time to set.
     */
//    public void setStartTime( long startTime );

    /**
     * @return Returns the end time.
     */
    public long getEndTime();

    /**
     * @param endTime The end time to set.
     */
//    public void setEndTime( long endTime );

    /**
     * @return Returns the error.
     */
    public Throwable getError();

//    /**
//     * @param error The error to set.
//     */
//    public void setError( Throwable Error );
/*
    public ExecutionResponse getMaven2Result();

    public void setMaven2Result( ExecutionResponse executionResponse );
*/
    public ContinuumBuildResult getBuildResult();

//    public void setBuildResult( ContinuumBuildResult buildResult );
}
