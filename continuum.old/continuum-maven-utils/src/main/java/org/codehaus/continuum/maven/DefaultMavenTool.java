package org.codehaus.continuum.maven;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
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
import java.util.Iterator;
import java.util.List;

import org.apache.maven.Maven;
import org.apache.maven.ExecutionResponse;
import org.apache.maven.lifecycle.goal.GoalNotFoundException;
import org.apache.maven.model.CiManagement;
import org.apache.maven.model.Scm;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingException;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.utils.PlexusUtils;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultMavenTool.java,v 1.9 2004-10-30 00:00:49 jvanzyl Exp $
 */
public class DefaultMavenTool
    extends AbstractLogEnabled
    implements MavenTool, Initializable
{
    // Maven has a bit of a odd name just because getMaven() should be used
    // as it's required that the instance is lazily initialized.
    /** @requirement */
    private Maven maven;

    /** @configuration default="m2" */
	private String mavenBin;

    /** @configuration default ${maven.home} */
    private String mavenHome;

    /** @configuration default ${maven.home.local} */
    private String mavenHomeLocal;

    /** @configuration default ${maven.repo.local} */
    private String mavenRepository;

    private File classWorldsJar;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws Exception
    {
        PlexusUtils.assertRequirement( maven, Maven.ROLE );

        PlexusUtils.assertConfiguration( mavenBin, "maven-bin" );

        PlexusUtils.assertConfiguration( mavenHome, "maven-home" );
        PlexusUtils.assertConfiguration( mavenHomeLocal, "maven-home-local" );
        PlexusUtils.assertConfiguration( mavenRepository, "maven-repository" );

        // TODO: assert that mavenHome, mavenHomelocal and mavenRepository exists.

        File mavenHomeFile = new File( mavenHome );
        File mavenHomeLocalFile = new File( mavenHomeLocal );

        if ( mavenHome.equals( "${maven.home}" ) || !mavenHomeFile.isDirectory()  )
        {
            mavenHomeFile = locateMavenHome();

            mavenHome = mavenHomeFile.getAbsolutePath();
        }

        if ( mavenHomeLocal.equals( "${maven.home.local}" ) || !mavenHomeLocalFile.isDirectory() )
        {
            mavenHomeLocalFile = locateMavenHomeLocal();

            mavenHomeLocal = mavenHomeLocalFile.getAbsolutePath();
        }

        if ( mavenRepository.equals( "${maven.repo.local}" ) || mavenRepository == null )
        {
            mavenRepository = new File( mavenHomeLocalFile, "repository" ).getAbsolutePath();
        }

        maven.setMavenHome( mavenHomeFile );
        maven.setMavenHomeLocal( mavenHomeLocalFile );

        getLogger().info( "Using " + maven.getMavenHome().getAbsolutePath() + " as maven.home." );
        getLogger().info( "Using " + maven.getMavenHomeLocal().getAbsolutePath() + " as maven.home.local." );
//        getLogger().info( "Using " + mavenRepository + " as maven.repo.local" );

        getLogger().info( "Using '" + mavenBin + "' as the maven 2 executable" );
    }

    // ----------------------------------------------------------------------
    // MavenTool Implementation
    // ----------------------------------------------------------------------

    public MavenProject getProject( File file )
        throws ContinuumException
    {
        MavenProject project;

        try
        {
            project = maven.getProject( file );
        }
        catch( ProjectBuildingException ex )
        {
            throw new ContinuumException( "Error while building project.", ex );
        }

        // ----------------------------------------------------------------------
        // Validate the MavenProject after some Continuum rules
        // ----------------------------------------------------------------------

        // Nag email address
        CiManagement ciManagement = project.getCiManagement();

        if ( ciManagement == null )
        {
            throw new ContinuumException( "Missing CiManagement from the project descriptor." );
        }

        if ( StringUtils.isEmpty( ciManagement.getNagEmailAddress() ) )
        {
            throw new ContinuumException( "Missing nag email address from the continuous integration info." );
        }

        // SCM connection
        Scm scm = project.getScm();

        if ( scm == null )
        {
            throw new ContinuumException( "Missing Scm from the project descriptor." );
        }

        String url = scm.getConnection();

        if ( StringUtils.isEmpty( url ) )
        {
            throw new ContinuumException( "Missing anonymous scm connection url." );
        }

        // Version
        if ( StringUtils.isEmpty( project.getVersion() ) )
        {
            throw new ContinuumException( "Missing version from the project descriptor." );
        }

        return project;
    }

    public String getProjectName( MavenProject project )
    {
        String name = project.getName();

        if ( StringUtils.isEmpty( name ) )
        {
            name = project.getGroupId() + ":" + project.getArtifactId();
        }

        return name;
    }

    public String getScmUrl( MavenProject project )
    {
        return project.getScm().getConnection();
    }

    public String getNagEmailAddress( MavenProject project )
    {
        return project.getCiManagement().getNagEmailAddress();
    }

    public String getVersion( MavenProject project )
    {
        return project.getVersion();
    }

    public ExecutionResponse execute( MavenProject project, List goals )
        throws ContinuumException
    {
        try
        {
            return maven.execute( project, goals );
        }
        catch ( GoalNotFoundException ex )
        {
            throw new ContinuumException( "Maven could not find the specified goal.", ex );
        }
    }

    public ExternalMavenExecutionResult executeExternal( File workingDirectory, List goals )
        throws ContinuumException
    {
        Commandline cl = getMaven2CommandLine( workingDirectory, goals );

        CommandLineUtils.StringStreamConsumer stderr = new CommandLineUtils.StringStreamConsumer();

        CommandLineUtils.StringStreamConsumer stdout = new CommandLineUtils.StringStreamConsumer();

        int exitCode;

        try
        {
            exitCode = CommandLineUtils.executeCommandLine( cl, stdout, stderr );
        }
        catch( Exception ex )
        {
            throw new ContinuumException( "Error while executing command.", ex );
        }

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        String out = stdout.getOutput();

        String err = stderr.getOutput();

        return new ExternalMavenExecutionResult( out, err, exitCode );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private Commandline getMaven2CommandLine( File workingDirectory, List goals )
    {
        Commandline cl = new Commandline();

        cl.setExecutable( mavenBin );

        cl.setWorkingDirectory( workingDirectory.getAbsolutePath() );

//        cl.createArgument().setValue( "-classpath" );
//
//        cl.createArgument().setValue( classWorldsJar.getAbsolutePath() );
//
//        cl.createArgument().setValue( "-Dclassworlds.conf=" + getMavenHome() + "/bin/classworlds.conf" );
//
//        cl.createArgument().setValue( "-Dmaven.home=" + getMavenHome() );
//
//        cl.createArgument().setValue( "-Dmaven.home.local=" + getMavenHomeLocal() );

        if ( !StringUtils.isEmpty( getMavenRepository() ) )
        {
            cl.createArgument().setValue( "-Dmaven.repo.local=" + getMavenRepository() );
        }

//        cl.createArgument().setValue( "org.codehaus.classworlds.Launcher" );

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

        getLogger().warn( "Executing external maven. Commandline: " + cmd );

        getLogger().warn( "Executing external maven. Working directory: " + cl.getWorkingDirectory().getAbsolutePath() );

        return cl;
    }

    // ----------------------------------------------------------------------
    // Getters
    // ----------------------------------------------------------------------

    public String getMavenHome()
    {
        return mavenHome;
    }

    public String getMavenHomeLocal()
    {
        return mavenHomeLocal;
    }

    public String getMavenRepository()
    {
        return mavenRepository;
    }

    // ----------------------------------------------------------------------
    // This should really go away
    // ----------------------------------------------------------------------

    public static File locateMavenHome()
    	throws ContinuumException
    {
        String mavenHome = System.getProperty( "maven.home" );

        if ( mavenHome != null )
        {
            return new File( mavenHome );
        }

        File tmp = new File( System.getProperty( "user.home" ), "m2" );

        if ( !tmp.isDirectory() )
        {
            throw new ContinuumException( "Could not find maven.home in '" + tmp.getAbsolutePath() + "'." );
        }

        return tmp;
    }

    public static File locateMavenHomeLocal()
    	throws ContinuumException
    {
        String mavenHomeLocal = System.getProperty( "maven.home.local" );

        if ( mavenHomeLocal != null )
        {
            return new File( mavenHomeLocal );
        }

        File tmp = new File( System.getProperty( "user.home" ), ".m2" );

        if ( !tmp.isDirectory() )
        {
            throw new ContinuumException( "Could not find maven.home.local in '" + tmp.getAbsolutePath() + "'." );
        }

        return tmp;
    }
}
