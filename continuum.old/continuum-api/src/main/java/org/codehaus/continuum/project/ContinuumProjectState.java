package org.codehaus.continuum.project;

import java.io.Serializable;

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
 * @version $Id: ContinuumProjectState.java,v 1.4 2004-07-29 03:43:48 trygvis Exp $
 */
public class ContinuumProjectState
    implements Serializable
{
    /**
     * This state indicates that the project is new and has never been build.
     */
    public final static ContinuumProjectState NEW = new ContinuumProjectState( "new" );

    /**
     * This state indicates that the project has been successfully build.
     */
    public final static ContinuumProjectState OK = new ContinuumProjectState( "ok" );

    /**
     * This state indicates that the project didn't build successfully.
     */
    public final static ContinuumProjectState FAILED = new ContinuumProjectState( "failed" );

    /**
     * This stats indicates that there was a error while building the project.
     *
     * A error while building the project might indicate that it couldn't
     * download the sources or other things that continuum doesn't have any
     * control over.
     */
    public final static ContinuumProjectState ERROR = new ContinuumProjectState( "error" );

    /**
     * This state indicates that this project has been placed on the build queue.
     *
     * Continuum can be configured with a delay from the first build signal to
     * the actual build starts to make.
     */
    public final static ContinuumProjectState BUILD_SIGNALED = new ContinuumProjectState( "signaled" );

    /**
     * This state indicates that a project is currently beeing build.
     */
    public final static ContinuumProjectState BUILDING = new ContinuumProjectState( "building" );

    private String name;

    protected ContinuumProjectState( String name )
    {
        this.name = name;
    }

    public String getI18nKey()
    {
        return "org.codehaus.continuum.project.state." + name;
    }

    public String toString()
    {
        return name;
    }
}
