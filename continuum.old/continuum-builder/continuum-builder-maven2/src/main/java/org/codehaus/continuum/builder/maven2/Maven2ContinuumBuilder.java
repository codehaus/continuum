package org.codehaus.continuum.builder.maven2;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.ExecutionResponse;
import org.apache.maven.Maven;
import org.apache.maven.lifecycle.goal.GoalNotFoundException;
import org.apache.maven.model.Build;
import org.apache.maven.model.CiManagement;
import org.apache.maven.model.Scm;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingException;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.builder.ContinuumBuilder;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumBuildResult;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ProjectDescriptor;
import org.codehaus.continuum.scm.ContinuumScm;
import org.codehaus.continuum.utils.PlexusUtils;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: Maven2ContinuumBuilder.java,v 1.7 2004-10-15 13:00:58 trygvis Exp $
 */
public abstract class Maven2ContinuumBuilder
    extends AbstractLogEnabled
    implements ContinuumBuilder
{
    /** @requirement */
    private ContinuumScm scm;

    // Maven has a bit of a odd name just because getMaven() should be used
    // as it's required that the instance is lazily initialized.
    /** @requirement */
    private Maven mavenInstance;

    /** @default ${maven.home} */
    private String mavenHome;

    /** @default ${maven.home.local} */
    private String mavenHomeLocal;

    /** @default ${maven.home}/repository */
    private String mavenRepository;

    private boolean mavenInitialized;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws Exception
    {
        PlexusUtils.assertRequirement( mavenInstance, Maven.ROLE );
        PlexusUtils.assertRequirement( scm, ContinuumScm.ROLE );

        PlexusUtils.assertConfiguration( mavenHome, "maven-home" );
        PlexusUtils.assertConfiguration( mavenHomeLocal, "maven-home-local" );
        PlexusUtils.assertConfiguration( mavenRepository, "maven-repository" );

        getMaven();
    }

    // ----------------------------------------------------------------------
    // Getters
    // ----------------------------------------------------------------------

    protected String getMavenHome()
    {
        return mavenHome;
    }

    protected String getMavenHomeLocal()
    {
        return mavenHomeLocal;
    }

    protected String getMavenRepository()
    {
        return mavenRepository;
    }

    // ----------------------------------------------------------------------
    // ContinuumBuilder implementation
    // ----------------------------------------------------------------------

    public ProjectDescriptor createDescriptor( ContinuumProject project )
        throws ContinuumException
    {
        Maven2ProjectDescriptor descriptor = new Maven2ProjectDescriptor();

        File basedir = scm.checkout( project );

        MavenProject mavenProject;

        try
        {
            File pomFile = getPomFile( basedir );

            mavenProject = getMaven().getProject( pomFile );

            List goals = new LinkedList();

            goals.add( "pom:install" );

            execute( basedir, mavenProject, null, goals );
        }
        catch( ProjectBuildingException ex )
        {
            throw new ContinuumException( "Error while building the project.", ex );
        }
        catch( GoalNotFoundException ex )
        {
            throw new ContinuumException( "Error while building the project.", ex );
        }

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

        // The public connection takes priority over the developer connection
        Scm scm = mavenProject.getScm();

        String scmConnection = scm.getConnection();

        if ( StringUtils.isEmpty( scmConnection ) )
        {
            scmConnection = scm.getDeveloperConnection();
        }

        if ( StringUtils.isEmpty( scmConnection ) )
        {
            throw new ContinuumException( "Missing both anonymous and developer scm connection urls." );
        }

        descriptor.setScmConnection( scmConnection );

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

        project.setScmConnection( scmConnection );

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

        Maven maven = getMaven();

        Maven2BuildResult result;

        descriptor = (Maven2ProjectDescriptor) build.getProject().getDescriptor();

        try
        {
            File file = getPomFile( workingDirectory );

            MavenProject pom;

            pom = maven.getProject( file );

            descriptor.setPom( IOUtil.toString( new FileInputStream( file ) ) );

            List goals = descriptor.getGoals();

            // TODO: get the output from the maven build and check for error
            ExecutionResponse executionResponse = maven.execute( pom, goals );

            // TODO: is this wanted?
            FileUtils.forceDelete( workingDirectory );

            result = new Maven2BuildResult( build, executionResponse );
        }
        catch ( ProjectBuildingException ex )
        {
            throw new ContinuumException( "Error while building the project metadata.", ex );
        }
        catch ( GoalNotFoundException ex )
        {
            throw new ContinuumException( "Maven could not find the specified goal.", ex );
        }
        catch ( IOException ex )
        {
            throw new ContinuumException( "IO Error.", ex );
        }

        return result;
    }

    // ----------------------------------------------------------------------
    // Other public methods
    // ----------------------------------------------------------------------

    public MavenProject getProject( File file )
        throws ContinuumException
    {
        try
        {
            return getMaven().getProject( file );
        }
        catch( ProjectBuildingException ex )
        {
            throw new ContinuumException( "Error while building project.", ex );
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    protected Maven2BuildResult execute( File workingDirectory, MavenProject mavenProject, ContinuumBuild build, List goals )
        throws GoalNotFoundException, ContinuumException
    {
        if ( true )
        {
            throw new ContinuumException( "Not implemented." );
        }

        ExecutionResponse response = getMaven().execute( mavenProject, goals );

        if ( response.isExecutionFailure() )
        {
            throw new ContinuumException( "Error while building the project. Failed goal: " + response.getFailedGoal() + "\nFailure message: \n" + response.getFailureResponse().longMessage() );
        }

        return new Maven2BuildResult( null, true );
    }

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

    private Maven getMaven()
        throws ContinuumException
    {
        if ( mavenInitialized )
        {
            return mavenInstance;
        }

        synchronized( mavenInstance )
        {
            if ( mavenInitialized )
            {
                return mavenInstance;
            }
            else
            {
                getLogger().info( "Using " + mavenHome + " as maven.home." );

                getLogger().info( "Using " + mavenHomeLocal + " as maven.home.local." );

                getLogger().info( "Using " + mavenRepository + " as maven.repo.local." );

                mavenInstance.setMavenHome( new File( mavenHome ) );

                mavenInstance.setMavenHomeLocal( new File( mavenHomeLocal ) );

                mavenInitialized = true;

                return mavenInstance;
            }
        }
    }
}