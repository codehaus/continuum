package org.codehaus.continuum.builder.maven.m2;

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

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URL;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.CiManagement;
import org.apache.maven.model.Scm;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultMavenBuilderHelper.java,v 1.2 2005-02-21 14:58:09 trygvis Exp $
 */
public class DefaultMavenBuilderHelper
    implements MavenBuilderHelper
{
    private MavenProjectBuilder projectBuilder;

    public ContinuumProject createProjectFromMetadata( URL metadata )
        throws ContinuumException
    {
        // ----------------------------------------------------------------------
        // We need to roll the project data into a file so that we can use it
        // ----------------------------------------------------------------------

        ContinuumProject project = new ContinuumProject();

        try
        {
            File f = createMetadataFile( metadata );

            mapMetadataToProject( f, project );
        }
        catch ( Exception e )
        {
            throw new ContinuumException( "Cannot create continuum project:", e );
        }

        return project;
    }

    public void updateProjectFromMetadata( File workingDirectory, ContinuumProject project )
        throws ContinuumException
    {
        File f = new File( workingDirectory, "pom.xml" );

        mapMetadataToProject( f, project );
    }

    protected File createMetadataFile( URL metadata )
        throws ContinuumException
    {
        try
        {
            InputStream is = metadata.openStream();

            File f = File.createTempFile( "continuum", "tmp" );

            FileWriter writer = new FileWriter( f );

            IOUtil.copy( is, writer );

            is.close();

            writer.close();

            return f;
        }
        catch ( Exception e )
        {
            throw new ContinuumException( "Cannot create metadata file:", e );
        }
    }


    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    protected void mapMetadataToProject( File metadata, ContinuumProject project )
        throws ContinuumException
    {
        MavenProject mavenProject = getProject( metadata );

        //project.setId( mavenProject.getId() );

        project.setNagEmailAddress( mavenProject.getCiManagement().getNagEmailAddress() );

        project.setName( mavenProject.getName() );

        project.setScmUrl( mavenProject.getScm().getConnection() );

        project.setVersion( mavenProject.getVersion() );
    }

    protected MavenProject getProject( File file )
        throws ContinuumException
    {
        ArtifactRepository r = new ArtifactRepository( "local", "file:///home/jvanzyl/maven-repo-local" );

        MavenProject project = null;

        try
        {
            project = projectBuilder.build( file, r );
        }
        catch ( ProjectBuildingException e )
        {
            throw new ContinuumException( "Cannot build maven project from " + file, e );
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
}
