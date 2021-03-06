package org.codehaus.continuum.project;

/*
 * Copyright (c) 2004, Jason van Zyl and Trygve Laugst�l
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
 * @version $Id: BuildResultState.java,v 1.2 2004-07-27 05:42:10 trygvis Exp $
 */
public class BuildResultState
{
    /** */
    public final static BuildResultState BUILDING = new BuildResultState( "building" );

    /** */
    public final static BuildResultState RESULT_OK = new BuildResultState( "ok" );

    /** */
    public final static BuildResultState RESULT_FAILURE = new BuildResultState( "failure" );

    /** */
    public final static BuildResultState RESULT_ERROR = new BuildResultState( "error" );

    private final String name;

    private BuildResultState( String name )
    {
        this.name = name;
    }

    public String getI18nKey()
    {
        return "org.codehaus.continuum.project.BuildResult.state." + name;
    }
}
