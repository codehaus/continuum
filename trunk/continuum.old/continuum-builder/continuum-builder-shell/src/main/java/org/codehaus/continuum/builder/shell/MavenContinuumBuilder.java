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

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ProjectDescriptor;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: MavenContinuumBuilder.java,v 1.3 2004-10-28 16:00:18 jvanzyl Exp $
 */
public class MavenContinuumBuilder
    extends ShellContinuumBuilder
{
    public ProjectDescriptor createDescriptor( ContinuumProject project )
        throws ContinuumException
    {
        ShellProjectDescriptor descriptor = new ShellProjectDescriptor();

        File basedir = scm.checkout( project );

        Xpp3Dom mavenProject;

        try
        {
            File pomFile = getPomFile( basedir );

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

        Xpp3Dom build = mavenProject.getChild( "build" );

        boolean isPom = true;

        if ( build != null )
        {
            String sourceDirectory = build.getChild( "sourceDirectory" ).getValue();

            if ( sourceDirectory != null && sourceDirectory.trim().length() > 0 )
            {
                if ( new File( sourceDirectory ).isDirectory() )
                {
                    isPom = false;
                }
            }
        }

        List goals = new ArrayList();

        if ( isPom )
        {
            goals.add( "pom:install" );
        }
        else
        {
            goals.add( "clean:clean" );

            goals.add( "jar:install" );
        }

        descriptor.getOptions().put( "goals", goals );

        descriptor.setName( mavenProject.getName() );

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

        descriptor.setScmConnection( scmConnection );

        String nagEmailAddress = build.getChild( "nagEmailAddress" ).getValue();

        if ( StringUtils.isEmpty( nagEmailAddress ) )
        {
            throw new ContinuumException( "Missing nag email address from the ci section of the project descriptor." );
        }

        descriptor.setNagEmailAddress( nagEmailAddress );

        String version = mavenProject.getChild( "currentVersion" ).getValue();

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

        project.setScmUrl( scmConnection );

        project.setNagEmailAddress( nagEmailAddress );

        project.setVersion( version );

        return descriptor;
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

        cl.setExecutable( shellCommand );

        cl.setWorkingDirectory( workingDirectory.getAbsolutePath() );

        List goals = (List) projectDescriptor.getOptions().get( "goals" );

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

        return cl;
    }
}
