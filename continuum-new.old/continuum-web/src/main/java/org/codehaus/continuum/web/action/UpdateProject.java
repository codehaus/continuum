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

import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.plexus.summit.SummitConstants;
import org.codehaus.plexus.summit.rundata.RunData;
import org.codehaus.plexus.summit.view.ViewContext;

import java.util.Map;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: UpdateProject.java,v 1.2 2005-03-10 00:05:57 trygvis Exp $
 */
public class UpdateProject
    extends AbstractAction
{
    public void execute( Map request )
        throws Exception
    {
        RunData data = (RunData) request.get( "data" );

        ViewContext vc = (ViewContext) data.getMap().get( SummitConstants.VIEW_CONTEXT );

        String projectId = (String) request.get( "id" );

        String button = (String) request.get( "button" );

        ContinuumProject project;

        if ( button != null && button.trim().length() > 0 && button.equals( "Update" ) )
        {
            project = getContinuumStore().getProject( projectId );

            String name = (String) request.get( "name" );

            String scmConnection = (String) request.get( "scmConnection" );

            String nagEmailAddress = (String) request.get( "nagEmailAddress" );

            String version = (String) request.get( "version" );

            //!!! This should all be done through continuum. This is just wrong

            getContinuumStore().updateProject( projectId, name, scmConnection, nagEmailAddress, version, null );
        }

        project = getContinuumStore().getProject( projectId );

        vc.put( "project", project );
    }
}
