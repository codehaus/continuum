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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.ExecutionResponse;
import org.apache.maven.GoalNotFoundException;
import org.apache.maven.Maven;
import org.apache.maven.model.Build;
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
import org.codehaus.continuum.utils.PomV3ToV4Converter;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: Maven2ContinuumBuilder.java,v 1.10 2004-07-27 05:42:12 trygvis Exp $
 */
public class Maven2ContinuumBuilder
    extends AbstractLogEnabled
    implements ContinuumBuilder, Initializable
{
    /** @requirement */
    private ContinuumScm scm;

    // Maven has a bit of a odd name just because getMaven() should be used
    // as it's required that the instance is lazily initialized.
    /** @requirement */
    private Maven mavenInstance;

    /** @default ${maven.home} */
    private String mavenHome;

    /** @default ${maven.home}/repository */
    private String mavenLocalRepository;

    private boolean mavenInitialized;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws Exception
    {
        PlexusUtils.assertRequirement( mavenInstance, Maven.ROLE );
        PlexusUtils.assertRequirement( scm, ContinuumScm.ROLE );
    }

    // ----------------------------------------------------------------------
    // ContinuumBuilder implementation
    // ----------------------------------------------------------------------

    public ProjectDescriptor createDescriptor( ContinuumProject project )
        throws ContinuumException
    {
        Maven2ProjectDescriptor descriptor = new Maven2ProjectDescriptor();

        File basedir = scm.checkout( project );

        String pom;

        File pomFile;

        try
        {
            pomFile = getPomFile( basedir );

            pom = IOUtil.toString( new FileReader( pomFile ) );
        }
        catch( FileNotFoundException ex )
        {
            throw new ContinuumException( "Could not find the file 'pom.xml' in the base directory of the checkout." );
        }
        catch( IOException ex )
        {
            throw new ContinuumException( "Error while reading the file 'pom.xml' in the base directory of the checkout." );
        }

        descriptor.setPom( pom );

        List goals = new LinkedList();

        goals.add( "pom:install" );

        MavenProject mavenProject;

        try
        {
            mavenProject = getMaven().getProject( pomFile );

            ExecutionResponse response = getMaven().execute( mavenProject, goals );

            if ( response.isExecutionFailure() )
            {
                throw new ContinuumException( "Error while building the project. Failed goal: " + response.getFailedGoal() + "\nFailure message: \n" + response.getFailureResponse().longMessage() );
            }
        }
        catch( ProjectBuildingException ex )
        {
            throw new ContinuumException( "Error while building the project.", ex );
        }
        catch( GoalNotFoundException ex )
        {
            throw new ContinuumException( "Error while building the project.", ex );
        }

        // TODO: Pick out the email addresses from the pom and store it
        // in the generic project descriptor

        Build build = mavenProject.getBuild();

        boolean isPom = true;

        if ( build != null )
        {
            String sourceDirectory = build.getSourceDirectory();

            if ( sourceDirectory != null && sourceDirectory.trim().length() > 0 )
            {
                getLogger().warn( "new File( sourceDirectory ): " + new File( sourceDirectory ) );

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

        return descriptor;
    }

    public synchronized ContinuumBuildResult build( File workingDirectory, ContinuumBuild build )
        throws ContinuumException
    {
//        String basedir = null;

//        BuildResult build;

        Maven2ProjectDescriptor descriptor;

        Maven maven = getMaven();

//        Exception exception = null;

        Maven2BuildResult result;

        descriptor = (Maven2ProjectDescriptor) build.getProject().getDescriptor();

/*
        try
        {
            build = store.getBuildResult( buildId );
        }
        catch( ContinuumStoreException ex )
        {
            notifier.buildComplete( null );

            setBuildResult( buildId, ContinuumProjectState.ERROR, ex );

            return;
        }
*/
//        ContinuumProject continuumProject = build.getProject();

//        notifier.buildStarted( build );
/*
        try
        {
            notifier.checkoutStarted( build );

            scm.clean( continuumProject );

            basedir = scm.checkout( continuumProject );

            notifier.checkoutComplete( build );
        }
        catch( Exception ex )
        {
            build.setError( ex );

            notifier.checkoutComplete( build );

            setBuildResult( buildId, ContinuumProjectState.ERROR, ex );

            return;
        }
*/
        try
        {
//            notifier.buildStarted( build );

//            File file = new File( basedir, "pom.xml" );

            File file = getPomFile( workingDirectory );

            MavenProject pom;

            pom = maven.getProject( file );

            descriptor.setMavenProject( pom );

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
/*
        ContinuumProjectState result;

        if ( exception == null )
        {
            result = ContinuumProjectState.OK;
        }
        else
        {
            result = ContinuumProjectState.ERROR;
        }

        setBuildResult( buildId, result, exception, executionResponse );

        build.setError( exception );

        notifier.buildComplete( build );
*/
        return result;
    }
/*
    private void setBuildResult( String buildId, ContinuumProjectState state )
    {
        setBuildResult( buildId, state, null, null );
    }

    private void setBuildResult( String buildId, ContinuumProjectState state, Exception ex )
    {
        setBuildResult( buildId, state, ex, null );
    }

    private void setBuildResult( String buildId, ContinuumProjectState state, Exception ex, ExecutionResponse executionResponse )
    {
        try
        {
            store.setBuildResult( buildId, state, ex, executionResponse );
        }
        catch( ContinuumStoreException ex2 )
        {
            getLogger().fatalError( "Error while persising build state.", ex2 );
        }
    }
*/
    private File getPomFile( File basedir )
        throws ContinuumException
    {
        File pomFile = new File( basedir, "pom.xml" );

        if ( pomFile.isFile() )
        {
            return pomFile;
        }

        File projectXmlFile = new File( basedir, "project.xml" );

        if( !projectXmlFile.isFile() )
        {
            throw new ContinuumException( "Could not find either Maven 1 or Maven 2 project descriptor." );
        }

        getLogger().info( "Found Maven 1 descriptor." );

        PomV3ToV4Converter converter = new PomV3ToV4Converter();

        try
        {
            converter.convertFile( projectXmlFile );
        }
        catch( Exception ex )
        {
            throw new ContinuumException( "Could not convert the Maven 1 project descriptor.", ex );
        }

        return pomFile;
    }

    private Maven getMaven()
        throws ContinuumException
    {
        synchronized( mavenInstance )
        {
            if ( !mavenInitialized )
            {
                getLogger().info( "Using " + mavenHome + " as maven home." );

                getLogger().info( "Using " + mavenLocalRepository + " as the maven repository." );

                mavenInstance.setMavenHome( mavenHome );

                mavenInstance.setLocalRepository( mavenLocalRepository );

                getLogger().info( "Booting maven." );

                try
                {
                    mavenInstance.booty();
                }
                catch( Exception ex )
                {
                    throw new ContinuumException( "Cound not initialize Maven.", ex);
                }

                mavenInitialized = true;
            }
        }

        return mavenInstance;
    }
}
