package org.codehaus.continuum.web.action;

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

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.codehaus.plexus.summit.SummitConstants;
import org.codehaus.plexus.summit.rundata.RunData;
import org.codehaus.plexus.summit.view.ViewContext;
import org.codehaus.continuum.builder.ant.AntBuilder;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AddAntProjectAction.java,v 1.2 2005-03-10 00:05:57 trygvis Exp $
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
