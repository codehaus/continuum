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

import java.util.Map;

import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.store.ContinuumStoreException;
import org.codehaus.continuum.web.utils.WebUtils;
import org.codehaus.plexus.i18n.I18N;
import org.codehaus.plexus.summit.SummitConstants;
import org.codehaus.plexus.summit.rundata.RunData;
import org.codehaus.plexus.summit.view.ViewContext;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: UpdateProject.java,v 1.1 2004-07-27 05:16:12 trygvis Exp $
 */
public class UpdateProject
    extends AbstractAction
{
    /** @requirement */
    private I18N i18n;

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
            try
            {
                project = getContinuumStore().getProject( projectId );
            }
            catch( ContinuumStoreException ex )
            {
                handleError( request, "Project doesn't exist." );

                return;
            }

            String name = (String) request.get( "name" );

            String scmConnection = (String) request.get( "scmConnection" );

            getContinuumStore().updateProject( projectId, name, scmConnection );
        }

        project = getContinuumStore().getProject( projectId );

        vc.put( "project", WebUtils.projectToProjectModel( i18n, project ) );
    }
}
