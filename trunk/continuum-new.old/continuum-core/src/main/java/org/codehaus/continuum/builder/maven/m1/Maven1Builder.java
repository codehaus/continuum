package org.codehaus.continuum.builder.maven.m1;

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
import java.io.FileReader;
import java.net.URL;
import java.util.Properties;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.builder.AbstractContinuumBuilder;
import org.codehaus.continuum.builder.ContinuumBuilder;
import org.codehaus.continuum.builder.shell.ExecutionResult;
import org.codehaus.continuum.builder.shell.ShellCommandHelper;
import org.codehaus.continuum.project.ContinuumBuildResult;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: Maven1Builder.java,v 1.3 2005-03-09 23:01:44 trygvis Exp $
 */
public class Maven1Builder
    extends AbstractContinuumBuilder
    implements ContinuumBuilder
{
    public final static String CONFIGURATION_GOALS = "goals";

    /** @requirement */
    private ShellCommandHelper shellCommandHelper;

    /** @configuration */
    private String mavenCommand;

    // ----------------------------------------------------------------------
    // Builder Implementation
    // ----------------------------------------------------------------------

    public ContinuumBuildResult build( ContinuumProject project )
        throws ContinuumException
    {
        Properties configuration = project.getConfiguration();

        File workingDirectory = new File( project.getWorkingDirectory() );

        String[] goals = StringUtils.split( configuration.getProperty( CONFIGURATION_GOALS ), "," );

        ExecutionResult executionResult;

        try
        {
            executionResult = shellCommandHelper.executeShellCommand( workingDirectory, mavenCommand, goals );
        }
        catch ( Exception e )
        {
            throw new ContinuumException( "Error while executing shell command.", e );
        }

        boolean success = executionResult.getExitCode() == 0;

        Maven1BuildResult result = new Maven1BuildResult();

        result.setSuccess( success );

        result.setStandardOutput( executionResult.getStandardOutput() );

        result.setStandardError( executionResult.getStandardError() );

        result.setExitCode( executionResult.getExitCode() );

        return result;
    }

    public ContinuumProject createProjectFromMetadata( URL metadata )
        throws ContinuumException
    {
        File pomFile = createMetadataFile( metadata );

        ContinuumProject project = new ContinuumProject();

        mapMetadata( pomFile, project );

        return project;
    }

    public void updateProjectFromCheckOut( File workingDirectory, ContinuumProject project )
        throws ContinuumException
    {
        File projectXmlFile = getProjectXmlFile( workingDirectory );

        mapMetadata( projectXmlFile, project );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    protected void mapMetadata( File metadata, ContinuumProject project )
        throws ContinuumException
    {
        Xpp3Dom mavenProject;

        try
        {
            mavenProject = Xpp3DomBuilder.build( new FileReader( metadata ) );
        }
        catch ( Exception e )
        {
            throw new ContinuumException( "Error while reading maven POM.", e );
        }

        // ----------------------------------------------------------------------
        // Populating the descriptor
        // ----------------------------------------------------------------------

        // Name
        String name = mavenProject.getChild( "name" ).getValue();

        if ( StringUtils.isEmpty( name ) )
        {
            throw new ContinuumException( "Missing <name> from the project descriptor." );
        }

        // Scm
        Xpp3Dom scm = mavenProject.getChild( "repository" );

        if ( scm == null )
        {
            throw new ContinuumException( "The project descriptor is missing the SCM information." );
        }

        String scmConnection = scm.getChild( "connection" ).getValue();

        if ( StringUtils.isEmpty( scmConnection ) )
        {
            scmConnection = scm.getChild( "developerConnection" ).getValue();
        }

        if ( StringUtils.isEmpty( scmConnection ) )
        {
            throw new ContinuumException( "Missing both anonymous and developer scm connection urls." );
        }

        // Nag email address
        Xpp3Dom build = mavenProject.getChild( "build" );

        String nagEmailAddress = build.getChild( "nagEmailAddress" ).getValue();

        if ( StringUtils.isEmpty( nagEmailAddress ) )
        {
            throw new ContinuumException( "Missing nag email address from the ci section of the project descriptor." );
        }

        // Version
        String version = mavenProject.getChild( "currentVersion" ).getValue();

        if ( StringUtils.isEmpty( version ) )
        {
            throw new ContinuumException( "Missing version from the project descriptor." );
        }

        // Goals
        Properties configuration = new Properties();

        configuration.setProperty( CONFIGURATION_GOALS, "clean:clean,jar:install" );

        // ----------------------------------------------------------------------
        // Make the project
        // ----------------------------------------------------------------------

        project.setName( name );

        project.setScmUrl( scmConnection );

        project.setNagEmailAddress( nagEmailAddress );

        project.setVersion( version );

        project.setConfiguration( configuration );
    }

    private File getProjectXmlFile( File basedir )
        throws ContinuumException
    {
        System.err.println( "basedir: " + basedir );

        File projectXmlFile = new File( basedir, "project.xml" );

        if ( !projectXmlFile.isFile() )
        {
            throw new ContinuumException( "Could not find Maven project descriptor." );
        }

        return projectXmlFile;
    }
}
