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

import org.apache.maven.model.CiManagement;
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
import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AddProjectPomUrl.java,v 1.4 2004-10-15 13:01:10 trygvis Exp $
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

        // download the pom.
        URL url = new URL( urlString );

        String pom = IOUtil.toString( url.openStream() );

        pomFile = File.createTempFile( "continuum-", "-pom-download" );

        FileUtils.fileWrite( pomFile.getAbsolutePath(), pom );

        MavenProject project = ((Maven2ContinuumBuilder) m2Builder).getProject( pomFile );

        String name = getName( project );

        String scmUrl = getScmUrl( project );

        String nagEmailAddress = getNagEmailAddress( project );

        String version = getVersion( project );

        getLogger().info( "Adding '" + name + "', scm url: '" + scmUrl + "', nag email address: '" + nagEmailAddress + "'." );

        getContinuum().addProject( name, scmUrl, nagEmailAddress, version, "maven2" );

        RunData data = (RunData) request.get( "data" );

        ViewContext vc = (ViewContext) data.getMap().get( SummitConstants.VIEW_CONTEXT );

        Iterator projects = getContinuumStore().getAllProjects();

        vc.put( "projects", WebUtils.projectsToProjectModels( getI18N(), projects ) );
    }

    private String getName( MavenProject project )
    {
        String name = project.getName();

        if ( StringUtils.isEmpty( name ) )
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

        if ( StringUtils.isEmpty( url ) )
        {
            throw new ContinuumException( "Missing anonymous scm connection url." );
        }

        return url;
    }

    private String getNagEmailAddress( MavenProject project )
        throws ContinuumException
    {
        CiManagement ciManagement = project.getCiManagement();

        if ( ciManagement == null )
        {
            throw new ContinuumException( "Missing CiManagement from the project descriptor." );
        }

        String nagEmailAddress = ciManagement.getNagEmailAddress();

        if ( StringUtils.isEmpty( nagEmailAddress ) )
        {
            throw new ContinuumException( "Missing nag email address from the continuous integration info." );
        }

        return nagEmailAddress;
    }

    private String getVersion( MavenProject project )
        throws ContinuumException
    {
        String version = project.getVersion();

        if ( StringUtils.isEmpty( version ) )
        {
            throw new ContinuumException( "Missing version from the project descriptor." );
        }

        return version;
    }
}
