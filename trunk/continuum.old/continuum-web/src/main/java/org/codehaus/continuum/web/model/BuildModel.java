package org.codehaus.continuum.web.model;

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
 * @version $Id: BuildModel.java,v 1.3 2004-10-06 14:24:26 trygvis Exp $
 */
public class BuildModel
{
    private String id;

    private String startTime;

    private String endTime;

    private String state;

    private BuildResultModel buildResult;

    /**
     * @param startTime
     * @param endTime
     * @param state
     */
    public BuildModel( String id, String startTime, String endTime, String state, BuildResultModel buildResult )
    {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.state = state;
        this.buildResult = buildResult;
    }

    /**
     * @return Returns the id.
     */
    public String getId()
    {
        return id;
    }

    /**
     * @return Returns the startTime.
     */
    public String getStartTime()
    {
        return startTime;
    }

    /**
     * @return Returns the endTime.
     */
    public String getEndTime()
    {
        return endTime;
    }

    /**
     * @return Returns the state.
     */
    public String getState()
    {
        return state;
    }

    /**
     * @return Returns the build result.
     */
    public BuildResultModel getResult()
    {
        return buildResult;
    }
}
