package org.codehaus.continuum.web.action;

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
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import org.apache.maven.model.Scm;
import org.apache.maven.project.MavenProject;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.builder.ContinuumBuilder;
import org.codehaus.continuum.builder.maven2.Maven2ContinuumBuilder;
import org.codehaus.continuum.web.utils.WebUtils;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.summit.SummitConstants;
import org.codehaus.plexus.summit.rundata.RunData;
import org.codehaus.plexus.summit.view.ViewContext;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AddProjectPomUrl.java,v 1.3 2004-10-06 14:24:24 trygvis Exp $
 */
public class AddProjectPomUrl
    extends AbstractAction
    implements Initializable
{
    /** @requirement */
    private ContinuumBuilder m2Builder;

    public void initialize()
        throws Exception
    {
        if ( !( m2Builder instanceof Maven2ContinuumBuilder ) )
        {
            throw new Exception( "The builder has to be a Maven2ContinuumBuilder." );
        }
    }

    public void execute( Map request )
        throws Exception
    {
        String urlString = (String) request.get( "addProject.pomUrl" );

        getLogger().info( "Downloading '" + urlString + "'." );

        File pomFile;

//        try
//        {
            // download the pom.
            URL url = new URL( urlString );

            String pom = IOUtil.toString( url.openStream() );

            pomFile = File.createTempFile( "continuum-", "-pom-download" );

            FileUtils.fileWrite( pomFile.getAbsolutePath(), pom );
//        }
//        catch( MalformedURLException ex )
//        {
//            handleError( request, "The URL is not correct." );
//
//            return;
//        }
//        catch( FileNotFoundException ex )
//        {
//            handleError( request, "Could not download the pom: The file doesn't exist." );
//
//            return;
//        }
//        catch( Exception ex )
//        {
//            handleError( request, "Could not download the pom.", ex );
//
//            return;
//        }

//        try
//        {
            MavenProject project = ((Maven2ContinuumBuilder) m2Builder).getProject( pomFile );

            getContinuum().addProject( getName( project ), getScmUrl( project ), "maven2" );
//        }
//        catch( ContinuumException ex )
//        {
//            handleError( request, "Error while adding the project.", ex );
//
//            return;
//        }

        RunData data = (RunData) request.get( "data" );

        ViewContext vc = (ViewContext) data.getMap().get( SummitConstants.VIEW_CONTEXT );

        Iterator projects;

//        try
//        {
            projects = getContinuumStore().getAllProjects();
//        }
//        catch( ContinuumStoreException ex )
//        {
//            handleError( request, "Error while getting projects.", ex );
//
//            return;
//        }

        vc.put( "projects", WebUtils.projectsToProjectModels( getI18N(), projects ) );
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
