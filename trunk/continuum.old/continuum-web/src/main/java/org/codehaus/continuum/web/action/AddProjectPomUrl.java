package org.codehaus.continuum.web.action;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.apache.maven.Maven;
import org.apache.maven.model.Scm;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingException;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AddProjectPomUrl.java,v 1.1 2004-07-27 05:16:12 trygvis Exp $
 */
public class AddProjectPomUrl
    extends AbstractAction
{
    /** @requirement */
    private Maven maven;

    public void execute( Map request )
        throws ContinuumException
    {
        String urlString = (String) request.get( "addProject.pomUrl" );

        getLogger().info( "Downloading '" + urlString + "'." );

        File pomFile;

        try
        {
            // download the pom.
            URL url = new URL( urlString );

            String pom = IOUtil.toString( url.openStream() );

            pomFile = File.createTempFile( "continuum-", "-pom-download" );

            FileUtils.fileWrite( pomFile.getAbsolutePath(), pom );
        }
        catch( MalformedURLException ex )
        {
            handleError( request, "The URL is not correct." );

            return;
        }
        catch( FileNotFoundException ex )
        {
            handleError( request, "Could not download the pom: The file doesn't exist." );

            return;
        }
        catch( IOException ex )
        {
            handleError( request, "Could not download the pom.", ex );

            return;
        }

        try
        {
            MavenProject project = maven.getProject( pomFile );

            getContinuum().addProject( getName( project ), getScmUrl( project ), "maven2" );
        }
        catch( ProjectBuildingException ex )
        {
            handleError( request, "Error while building the pom.", ex );
        }
        catch( ContinuumException ex )
        {
            handleError( request, "Error while adding the project.", ex );
        }
    }

    private String getName( MavenProject project )
    {
        String name = project.getName();

        if ( name == null )
        {
            name = project.getGroupId() + ":" + project.getArtifactId();
        }

        return name;
    }

    private String getScmUrl( MavenProject project )
        throws ContinuumException
    {
        Scm scm = project.getScm();

        if ( scm == null )
        {
            throw new ContinuumException( "Missing Scm from the project descriptor." );
        }

        String url = scm.getConnection();

        if ( url == null || url.trim().length() == 0 )
        {
            throw new ContinuumException( "Missing anonymous scm connection url." );
        }

        return url;
    }
}
