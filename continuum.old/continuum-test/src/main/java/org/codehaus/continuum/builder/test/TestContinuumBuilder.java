package org.codehaus.continuum.builder.test;

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

import java.io.File;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.builder.ContinuumBuilder;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumBuildResult;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ProjectDescriptor;
import org.codehaus.plexus.logging.AbstractLogEnabled;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: TestContinuumBuilder.java,v 1.3 2004-10-06 13:33:49 trygvis Exp $
 */
public class TestContinuumBuilder
    extends AbstractLogEnabled
    implements ContinuumBuilder
{
    /** Number of builds done. */
    private int buildCount = 0;

    private int buildSleepInterval = 0;

    private boolean failOnNextBuild;

    // ----------------------------------------------------------------------
    // ContinuumBuilder Implementation
    // ----------------------------------------------------------------------

    public ProjectDescriptor createDescriptor( ContinuumProject project )
        throws ContinuumException
    {
        ProjectDescriptor projectDescriptor = new TestProjectDescriptor();

        projectDescriptor.setProject( project );

        projectDescriptor.setProjectId( project.getId() );

        return projectDescriptor;
    }

    public ContinuumBuildResult build( File workingDirectory, ContinuumBuild build )
        throws ContinuumException
    {
        getLogger().info( "Building " + build.getId() );

        buildCount++;

        try
        {
            Thread.sleep( buildSleepInterval );
        }
        catch( Exception ex )
        {
            // ignore
        }

        if ( failOnNextBuild )
        {
            failOnNextBuild = false;

            return new TestBuildResult( build, false );
        }

        return new TestBuildResult( build, true );
    }

    // ----------------------------------------------------------------------
    // Misc
    // ----------------------------------------------------------------------

    public void setBuildSleepInterval( int buildSleepInterval )
    {
        this.buildSleepInterval = buildSleepInterval;
    }

    public void setFailOnNextBuild( boolean failOnNextBuild )
    {
        this.failOnNextBuild = failOnNextBuild;
    }

    public int getBuildCount()
    {
        return buildCount;
    }
}
