package org.codehaus.continuum.builder.test;

/*
 * LICENSE
 */

import java.io.File;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.builder.ContinuumBuilder;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumBuildResult;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ProjectDescriptor;
import org.codehaus.continuum.project.TestProjectDescriptor;
import org.codehaus.plexus.logging.AbstractLogEnabled;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: TestContinuumBuilder.java,v 1.1 2004-07-27 00:06:10 trygvis Exp $
 */
public class TestContinuumBuilder
    extends AbstractLogEnabled
    implements ContinuumBuilder
{
    /** Number of builds done. */
    private int buildCount = 0;

    private int buildSleepInterval = 0;

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

        return new TestBuildResult( true );
    }

    // ----------------------------------------------------------------------
    // Misc
    // ----------------------------------------------------------------------

    public void setBuildSleepInterval( int buildSleepInterval )
    {
        this.buildSleepInterval = buildSleepInterval;
    }

    public int getBuildCount()
    {
        return buildCount;
    }
}
