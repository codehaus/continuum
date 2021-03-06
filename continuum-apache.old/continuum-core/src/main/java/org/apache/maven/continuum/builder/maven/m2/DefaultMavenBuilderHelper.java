package org.apache.maven.continuum.builder.maven.m2;

/*
 * Copyright 2004-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.continuum.ContinuumException;
import org.apache.maven.continuum.project.ContinuumProject;
import org.apache.maven.model.CiManagement;
import org.apache.maven.model.Notifier;
import org.apache.maven.model.Repository;
import org.apache.maven.model.Scm;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.settings.MavenSettings;
import org.apache.maven.settings.MavenSettingsBuilder;

import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultMavenBuilderHelper.java,v 1.1.1.1 2005-03-29 20:42:00 trygvis Exp $
 */
public class DefaultMavenBuilderHelper
    implements MavenBuilderHelper
{
    /** @requirement */
    private MavenProjectBuilder projectBuilder;

    /** @requirement */
    private ArtifactRepositoryFactory artifactRepositoryFactory;

    /** @requirement */
    private MavenSettingsBuilder settingsBuilder;

    /** @requirement */
    private ArtifactRepositoryLayout repositoryLayout;

    /** @configuration */
    private String localRepository;

    // ----------------------------------------------------------------------
    // MavenBuilderHelper Implementation
    // ----------------------------------------------------------------------

    public ContinuumProject createProjectFromMetadata( URL metadata )
        throws ContinuumException
    {
        // ----------------------------------------------------------------------
        // We need to roll the project data into a file so that we can use it
        // ----------------------------------------------------------------------

        ContinuumProject project = new ContinuumProject();

        try
        {
            File file = createMetadataFile( metadata );

            mapMetadataToProject( file, project );
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

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

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
        for ( Iterator it = project.getCiManagement().getNotifiers().iterator(); it.hasNext(); )
        {
            Notifier notifier = (Notifier) it.next();

            if ( notifier.getType().equals( "mail" ) )
            {
                return notifier.getAddress();
            }
        }

        return null;
    }

    public String getVersion( MavenProject project )
    {
        return project.getVersion();
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

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

    protected void mapMetadataToProject( File metadata, ContinuumProject project )
        throws ContinuumException
    {
        MavenProject mavenProject = getProject( metadata );

        project.setNagEmailAddress( getNagEmailAddress( mavenProject ) );

        project.setName( getProjectName( mavenProject ) );

        project.setScmUrl( getScmUrl( mavenProject ) );

        project.setVersion( getVersion( mavenProject ) );

        Properties configuration = project.getConfiguration();

        if ( !configuration.containsKey( MavenShellBuilder.CONFIGURATION_GOALS ) )
        {
            configuration.setProperty( MavenShellBuilder.CONFIGURATION_GOALS, "clean:clean, install" );
        }
    }

    protected MavenProject getProject( File file )
        throws ContinuumException
    {
        MavenProject project = null;

        try
        {
            project = projectBuilder.build( file, getRepository() );
        }
        catch ( ProjectBuildingException e )
        {
            throw new ContinuumException( "Cannot build maven project from " + file, e );
        }

        // ----------------------------------------------------------------------
        // Validate the MavenProject using some Continuum rules
        // ----------------------------------------------------------------------

        // Nag email address
        CiManagement ciManagement = project.getCiManagement();

        if ( ciManagement == null )
        {
            throw new ContinuumException( "Missing CiManagement from the project descriptor." );
        }

        if ( StringUtils.isEmpty( getNagEmailAddress( project ) ) )
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

    private ArtifactRepository getRepository()
        throws ContinuumException
    {
        MavenSettings settings;

        try
        {
            settings = settingsBuilder.buildSettings();
        }
        catch ( Exception e )
        {
            throw new ContinuumException( "Error while building settings.", e );
        }

        Repository repository = new Repository();

        repository.setId( "local" );

        repository.setUrl( "file://" + localRepository );

        return artifactRepositoryFactory.createArtifactRepository( repository, settings, repositoryLayout );
    }
}
