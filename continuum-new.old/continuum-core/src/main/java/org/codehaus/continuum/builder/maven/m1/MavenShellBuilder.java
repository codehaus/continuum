package org.codehaus.continuum.builder.maven.m1;

/*
 * The MIT License
 *
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

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.builder.shell.ShellBuilder;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ContinuumProjectState;
import org.codehaus.continuum.project.DefaultContinuumProject;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;

import java.io.File;
import java.io.FileReader;
import java.net.URL;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: MavenShellBuilder.java,v 1.1.1.1 2005-02-17 22:23:50 trygvis Exp $
 */
public class MavenShellBuilder
    extends ShellBuilder
{
    public ContinuumProject createProjectFromMetadata( URL metadata )
        throws ContinuumException
    {
        File pomFile = createMetadataFile( metadata );

        ContinuumProject project = new DefaultContinuumProject();

        mapMetadata( pomFile, project );

        project.setState( ContinuumProjectState.NEW );

        project.setBuilderId( "maven1" );

        return project;
    }

    public void updateProjectFromMetadata( File workingDirectory, ContinuumProject project )
        throws ContinuumException
    {
        File pomFile = getPomFile( workingDirectory );

        mapMetadata( pomFile, project );
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

        project.setName( name );

        project.setScmUrl( scmConnection );

        project.setNagEmailAddress( nagEmailAddress );

        project.setVersion( version );
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
}
