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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ExternalMaven2ContinuumBuilder.java,v 1.1 2004-09-07 16:22:16 trygvis Exp $
 */
public class ExternalMaven2ContinuumBuilder
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

    /** @default ${user.home}/m2*/
    private String mavenHomeLocal;

    /** @default ${maven.home}/repository */
    private String mavenRepository;

    private boolean mavenInitialized;
/*
    private PrintStream originalSystemOut;

    private PrintStream originalSystemErr;

    private Maven2PrintStream systemOutLogger;

    private Maven2PrintStream systemErrLogger;
*/
    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws Exception
    {
        PlexusUtils.assertRequirement( scm, ContinuumScm.ROLE );

        PlexusUtils.assertConfiguration( mavenHome, "maven-home" );
        PlexusUtils.assertConfiguration( mavenHomeLocal, "maven-home-local" );
        PlexusUtils.assertConfiguration( mavenRepository, "maven-repository" );

        System.setProperty( "maven.repo.local", mavenRepository );

        getLogger().info( "Maven home: " + mavenHome );
        getLogger().info( "Maven local home: " + mavenHomeLocal );
        getLogger().info( "Maven repository: " + mavenRepository );

        getMaven();
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
        }
        catch( ProjectBuildingException ex )
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
        Maven2ProjectDescriptor descriptor;

        Maven maven = getMaven();

        Maven2BuildResult result;

        descriptor = (Maven2ProjectDescriptor) build.getProject().getDescriptor();

        try
        {
            File file = getPomFile( workingDirectory );

            MavenProject pom;

            pom = maven.getProject( file );

            descriptor.setMavenProject( pom );

            List goals = descriptor.getGoals();

            // ----------------------------------------------------------------------
            // Build it
            // ----------------------------------------------------------------------

            Commandline cl = new Commandline();

            cl.setExecutable( "java" );

            cl.setWorkingDirectory( workingDirectory.getAbsolutePath() );

            File classWorldsJar = new File( mavenHome, "/core/classworlds-1.1-SNAPSHOT.jar" );

            cl.createArgument().setValue( "-classpath" );

            cl.createArgument().setValue( classWorldsJar.getAbsolutePath() );

            cl.createArgument().setValue( "-Dclassworlds.conf=" + mavenHome + "/bin/classworlds.conf" );

            if ( !StringUtils.isEmpty( mavenRepository ) )
            {
                cl.createArgument().setValue( "-Dmaven.repo.local=" + mavenRepository );
            }

            cl.createArgument().setValue( "org.codehaus.classworlds.Launcher" );

            for ( Iterator it = goals.iterator(); it.hasNext(); )
            {
                String goal = (String) it.next();

                cl.createArgument().setValue( goal );
            }

            String[] args = cl.getCommandline();

            String cmd = args[0];

            for ( int i = 1; i < args.length; i++ )
            {
                cmd += " " + args[i];
            }

            getLogger().info( "Executing external maven. Commandline: " + cmd );

            CommandLineUtils.StringStreamConsumer stderr = new CommandLineUtils.StringStreamConsumer();

            CommandLineUtils.StringStreamConsumer stdout = new CommandLineUtils.StringStreamConsumer();

            int exitCode = CommandLineUtils.executeCommandLine( cl, stdout, stderr );

            // ----------------------------------------------------------------------
            //
            // ----------------------------------------------------------------------

            String out = stdout.getOutput();

            String err = stderr.getOutput();

            boolean success = out.indexOf( "BUILD SUCCESSFUL" ) != -1;

            result = new ExternalMaven2BuildResult( build, success, out, err, exitCode );
        }
        catch ( ProjectBuildingException ex )
        {
            throw new ContinuumException( "Error while building the project metadata.", ex );
        }

        return result;
    }

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

                getLogger().info( "Using " + mavenHomeLocal + " as the maven repository." );

                mavenInstance.setMavenHome( mavenHome );

                mavenInstance.setMavenHomeLocal( mavenHomeLocal );

                mavenInitialized = true;
            }
        }

        return mavenInstance;
    }
}
