package org.codehaus.continuum.web.action;

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

import org.codehaus.continuum.Continuum;
import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.builder.ContinuumBuilder;
import org.codehaus.continuum.store.tx.StoreTransactionManager;
import org.codehaus.continuum.web.utils.WebUtils;
import org.codehaus.plexus.summit.SummitConstants;
import org.codehaus.plexus.summit.rundata.RunData;
import org.codehaus.plexus.summit.view.ViewContext;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AddShellBuilderProject.java,v 1.2 2004-10-28 18:36:13 jvanzyl Exp $
 */
public class AddShellBuilderProject
    extends AbstractAction
{
    private Continuum continuum;

    private ContinuumBuilder builder;

    private StoreTransactionManager txManager;

    public void execute( Map request )
        throws Exception
    {
        String url = (String) request.get( "addProject.pomUrl" );

        getLogger().info( "Adding project from '" + url + "'." );

        addProjectFromUrl( new URL( url ) );

        RunData data = (RunData) request.get( "data" );

        ViewContext vc = (ViewContext) data.getMap().get( SummitConstants.VIEW_CONTEXT );

        Iterator projects = getContinuumStore().getAllProjects();

        vc.put( "projects", WebUtils.projectsToProjectModels( getI18N(), projects ) );
    }

    public String addProjectFromUrl( URL url )
        throws ContinuumException
    {
        try
        {
            String pom = IOUtil.toString( url.openStream() );

            File pomFile = File.createTempFile( "continuum-", "-pom-download" );

            FileUtils.fileWrite( pomFile.getAbsolutePath(), pom );

            Xpp3Dom project = Xpp3DomBuilder.build( new FileReader( pomFile ) );

            String name = project.getChild( "name" ).getValue();

            if ( name == null )
            {
                throw new ContinuumException( "Must have a name." );
            }

            String scmUrl = project.getChild( "repository" ).getChild( "connection" ).getValue();

            if ( scmUrl == null )
            {
                throw new ContinuumException( "Must have a repository/connection element." );
            }

            String nagEmailAddress = project.getChild( "build" ).getChild( "nagEmailAddress" ).getValue();

            if ( nagEmailAddress == null )
            {
                throw new ContinuumException( "Must have a build/nagEmailAddress." );
            }

            String version = project.getChild( "currentVersion" ).getValue();

            if ( version == null )
            {
                throw new ContinuumException( "Must have a currentVersion element." );
            }

            txManager.enter();

            String projectId = continuum.addProject( name, scmUrl, nagEmailAddress, version, "maven" );

            txManager.leave();

            return projectId;
        }
        catch ( Exception ex )
        {
            txManager.rollback();

            throw new ContinuumException( "Error while adding propject.", ex );
        }
    }
}
