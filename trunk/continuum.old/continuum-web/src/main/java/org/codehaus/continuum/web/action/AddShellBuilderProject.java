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

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.scm.ContinuumScmException;
import org.codehaus.continuum.web.utils.WebUtils;
import org.codehaus.plexus.summit.SummitConstants;
import org.codehaus.plexus.summit.rundata.RunData;
import org.codehaus.plexus.summit.view.ViewContext;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AddShellBuilderProject.java,v 1.3 2004-10-28 23:19:25 trygvis Exp $
 */
public class AddShellBuilderProject
    extends AbstractAction
{
    public void execute( Map request )
        throws Exception
    {
        String url = (String) request.get( "addProject.pomUrl" );

        getLogger().info( "Adding project from '" + url + "'." );

        String pom = IOUtil.toString( new URL( url ).openStream() );

        File pomFile = File.createTempFile( "continuum-", "-pom-download" );

        FileUtils.fileWrite( pomFile.getAbsolutePath(), pom );

        Xpp3Dom project = Xpp3DomBuilder.build( new FileReader( pomFile ) );

        String scmUrl = project.getChild( "repository" ).getChild( "connection" ).getValue();

        if ( scmUrl == null )
        {
            throw new ContinuumException( "Must have a repository/connection element." );
        }

        try
        {
            getContinuum().addProjectFromScm( scmUrl, "maven" );
        }
        catch ( ContinuumException ex )
        {
            if ( ex.getCause() instanceof ContinuumScmException )
            {
                ContinuumScmException scmEx = (ContinuumScmException) ex.getCause();

                getLogger().warn( scmEx.getResult().getCommandOutput() );
            }

            throw new ContinuumException( "Error while adding propject.", ex );
        }

        RunData data = (RunData) request.get( "data" );

        ViewContext vc = (ViewContext) data.getMap().get( SummitConstants.VIEW_CONTEXT );

        Iterator projects = getContinuumStore().getAllProjects();

        vc.put( "projects", WebUtils.projectsToProjectModels( getI18N(), projects ) );
    }
}
