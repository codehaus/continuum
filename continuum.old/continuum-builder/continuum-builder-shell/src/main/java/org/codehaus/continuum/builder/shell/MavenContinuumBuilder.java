package org.codehaus.continuum.builder.shell;

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
import java.io.FileReader;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.GenericContinuumProject;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: MavenContinuumBuilder.java,v 1.6 2004-10-29 15:03:44 trygvis Exp $
 */
public class MavenContinuumBuilder
    extends ShellContinuumBuilder
{
    public ContinuumProject createProject( File workingDirectory )
        throws ContinuumException
    {
        ShellProjectDescriptor descriptor = new ShellProjectDescriptor();

        Xpp3Dom mavenProject;

        try
        {
            File pomFile = getPomFile( workingDirectory );

            mavenProject = Xpp3DomBuilder.build( new FileReader( pomFile ) );
        }
        catch ( Exception e )
        {
            throw new ContinuumException( "Error while reading maven POM: ", e );
        }

        // ----------------------------------------------------------------------
        // Populating the descriptor
        // ----------------------------------------------------------------------

        if ( mavenProject.getChild( "repository" ) == null )
        {
            throw new ContinuumException( "The project descriptor is missing the SCM information." );
        }

        // Name
        String name = mavenProject.getChild( "name" ).getValue();

        if ( StringUtils.isEmpty( name ) )
        {
            throw new ContinuumException( "Missing <name> from the project descriptor." );
        }

        // Scm
        Xpp3Dom scm = mavenProject.getChild( "repository" );

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

        // ----------------------------------------------------------------------
        // Make the project
        // ----------------------------------------------------------------------

        ContinuumProject project = new GenericContinuumProject();

        project.setName( name );

        project.setScmUrl( scmConnection );

        project.setNagEmailAddress( nagEmailAddress );

        project.setVersion( version );

        project.setDescriptor( descriptor );

        return project;
    }

    private File getPomFile( File basedir )
        throws ContinuumException
    {
        File pomFile = new File( basedir, "project.xml" );

        if ( !pomFile.isFile() )
        {
            throw new ContinuumException( "Could not find Maven project descriptor." );
        }

        return pomFile;
    }

    // ----------------------------------------------------------------------
    // Create the command line:
    //
    // This should be generally configurable from a properties file or
    // some form of configuration so that it can be controlled from a
    // web interface.
    // ----------------------------------------------------------------------

    protected Commandline createCommandline( File workingDirectory, ShellProjectDescriptor projectDescriptor )
    {
        Commandline cl = new Commandline();

        String[] s = StringUtils.split( shellCommand );

        cl.setExecutable( s[0] );

        cl.setWorkingDirectory( workingDirectory.getAbsolutePath() );

        for ( int i = 1; i < s.length; i++ )
        {
            cl.createArgument().setValue( s[i] );
        }

        return cl;
    }
}
