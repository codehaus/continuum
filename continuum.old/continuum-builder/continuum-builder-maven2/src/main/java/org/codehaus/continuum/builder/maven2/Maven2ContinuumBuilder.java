package org.codehaus.continuum.builder.maven2;

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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.ExecutionResponse;
import org.apache.maven.model.Build;
import org.apache.maven.model.CiManagement;
import org.apache.maven.model.Scm;
import org.apache.maven.project.MavenProject;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.builder.ContinuumBuilder;
import org.codehaus.continuum.maven.MavenTool;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumBuildResult;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ProjectDescriptor;
import org.codehaus.continuum.scm.ContinuumScm;
import org.codehaus.continuum.scm.ContinuumScmException;
import org.codehaus.continuum.utils.PlexusUtils;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: Maven2ContinuumBuilder.java,v 1.9 2004-10-28 17:45:49 trygvis Exp $
 */
public abstract class Maven2ContinuumBuilder
    extends AbstractLogEnabled
    implements ContinuumBuilder
{
    /** @requirement */
    private ContinuumScm scm;

    /** @requirement */
    private MavenTool mavenTool;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws Exception
    {
        PlexusUtils.assertRequirement( mavenTool, MavenTool.ROLE );
        PlexusUtils.assertRequirement( scm, ContinuumScm.ROLE );
    }

    // ----------------------------------------------------------------------
    // ContinuumBuilder implementation
    // ----------------------------------------------------------------------

    public ProjectDescriptor createDescriptor( ContinuumProject project )
        throws ContinuumException
    {
        Maven2ProjectDescriptor descriptor = new Maven2ProjectDescriptor();

        try
        {
            scm.checkOutProject( project );
        }
        catch( ContinuumScmException ex )
        {
            throw new ContinuumException( "Error while checking out the project.", ex );
        }

        MavenProject mavenProject;

        File pomFile = getPomFile( new File( project.getWorkingDirectory() ) );

        mavenProject = mavenTool.getProject( pomFile );

        List goals = new LinkedList();

        goals.add( "pom:install" );

        mavenTool.execute( mavenProject, goals );

        // ----------------------------------------------------------------------
        // Populating the descriptor
        // ----------------------------------------------------------------------

        if ( mavenProject.getScm() == null )
        {
            throw new ContinuumException( "The project descriptor is missing the SCM section." );
        }

        if ( mavenProject.getCiManagement() == null )
        {
            throw new ContinuumException( "The project descriptor is missing the CI section." );
        }

        Build build = mavenProject.getBuild();

        boolean isPom = true;

        if ( build != null )
        {
            String sourceDirectory = build.getSourceDirectory();

            if ( sourceDirectory != null && sourceDirectory.trim().length() > 0 )
            {
                if ( new File( sourceDirectory ).isDirectory() )
                {
                    isPom = false;
                }
            }
        }

        if ( isPom )
        {
            descriptor.getGoals().add( "pom:install" );
        }
        else
        {
            descriptor.getGoals().add( "clean:clean" );

            descriptor.getGoals().add( "jar:install" );
        }

        descriptor.setName( mavenProject.getName() );

        // The public Url takes priority over the developer connection
        Scm scm = mavenProject.getScm();

        String scmUrl = scm.getConnection();

        if ( StringUtils.isEmpty( scmUrl ) )
        {
            scmUrl = scm.getDeveloperConnection();
        }

        if ( StringUtils.isEmpty( scmUrl ) )
        {
            throw new ContinuumException( "Missing both anonymous and developer scm connection urls." );
        }

        descriptor.setScmUrl( scmUrl );

        CiManagement ciManagement = mavenProject.getCiManagement();

        String nagEmailAddress = ciManagement.getNagEmailAddress();

        if ( StringUtils.isEmpty( nagEmailAddress ) )
        {
            throw new ContinuumException( "Missing nag email address from the ci section of the project descriptor." );
        }

        descriptor.setNagEmailAddress( nagEmailAddress );

        String version = mavenProject.getVersion();

        if ( StringUtils.isEmpty( version ) )
        {
            throw new ContinuumException( "Missing version from the project descriptor." );
        }

        descriptor.setVersion( version );

        // ----------------------------------------------------------------------
        // Update the project
        // ----------------------------------------------------------------------

        if ( !StringUtils.isEmpty( mavenProject.getName() ) )
        {
            project.setName( mavenProject.getName() );
        }

        project.setScmUrl( scmUrl );

        project.setNagEmailAddress( nagEmailAddress );

        project.setVersion( version );

        return descriptor;
    }

    public synchronized ContinuumBuildResult build( File workingDirectory, ContinuumBuild build )
        throws ContinuumException
    {
        if ( true )
        {
            throw new ContinuumException( "Not used." );
        }

        Maven2ProjectDescriptor descriptor;

        Maven2BuildResult result;

        descriptor = (Maven2ProjectDescriptor) build.getProject().getDescriptor();

        try
        {
            File file = getPomFile( workingDirectory );

            MavenProject pom;

            pom = mavenTool.getProject( file );

            descriptor.setPom( IOUtil.toString( new FileInputStream( file ) ) );

            List goals = descriptor.getGoals();

            // TODO: get the output from the maven build and check for error
            ExecutionResponse executionResponse = mavenTool.execute( pom, goals );

            // TODO: is this wanted?
            FileUtils.forceDelete( workingDirectory );

            result = new Maven2BuildResult( build, executionResponse );
        }
        catch ( IOException ex )
        {
            throw new ContinuumException( "IO Error.", ex );
        }

        return result;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------
/*
    protected Maven2BuildResult execute( File workingDirectory, MavenProject mavenProject, ContinuumBuild build, List goals )
        throws GoalNotFoundException, ContinuumException
    {
        if ( true )
        {
            throw new ContinuumException( "Not implemented." );
        }

        ExecutionResponse response = mavenTool.execute( mavenProject, goals );

        if ( response.isExecutionFailure() )
        {
            throw new ContinuumException( "Error while building the project. Failed goal: " + response.getFailedGoal() + "\nFailure message: \n" + response.getFailureResponse().longMessage() );
        }

        return new Maven2BuildResult( null, true );
    }
*/
    private File getPomFile( File basedir )
        throws ContinuumException
    {
        File pomFile = new File( basedir, "pom.xml" );

        if ( !pomFile.isFile() )
        {
            throw new ContinuumException( "Could not find Maven 2 project descriptor." );
        }

        return pomFile;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    protected MavenTool getMavenTool()
    {
        return mavenTool;
    }
}
