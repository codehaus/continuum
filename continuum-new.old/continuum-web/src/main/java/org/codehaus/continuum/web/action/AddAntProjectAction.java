package org.codehaus.continuum.web.action;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
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

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.codehaus.plexus.summit.SummitConstants;
import org.codehaus.plexus.summit.rundata.RunData;
import org.codehaus.plexus.summit.view.ViewContext;
import org.codehaus.continuum.builder.ant.AntBuilder;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AddAntProjectAction.java,v 1.1 2005-03-09 00:15:22 trygvis Exp $
 */
public class AddAntProjectAction
    extends AbstractAction
{
    public void execute( Map request )
        throws Exception
    {
        // ----------------------------------------------------------------------
        // Common project metadata
        // ----------------------------------------------------------------------

        String scmUrl = (String) request.get( "addProject.scmUrl" );

        String projectName = (String) request.get( "addProject.name" );

        String nagEmailAddress = (String) request.get( "addProject.nagEmailAddress" );

        String version = (String) request.get( "addProject.version" );

        // ----------------------------------------------------------------------
        // Ant builder configuration
        // ----------------------------------------------------------------------

        String executable = (String) request.get( "addAntProject.executable" );

        String targets = (String) request.get( "addAntProject.targets" );

        Properties configuration = new Properties();

        configuration.setProperty( AntBuilder.CONFIGURATION_EXECUTABLE, executable );

        configuration.setProperty( AntBuilder.CONFIGURATION_TARGETS, targets );

        // ----------------------------------------------------------------------
        // Add the project
        // ----------------------------------------------------------------------

        getLogger().info( "Adding project from scm '" + scmUrl + "'." );

        getContinuum().addProjectFromScm( scmUrl, "ant", projectName, nagEmailAddress, version, configuration );

        // ----------------------------------------------------------------------
        // Prepare the view context
        // ----------------------------------------------------------------------

        RunData data = (RunData) request.get( "data" );

        ViewContext vc = (ViewContext) data.getMap().get( SummitConstants.VIEW_CONTEXT );

        Iterator projects = getContinuumStore().getAllProjects();

        vc.put( "projects", projects );
    }
}
