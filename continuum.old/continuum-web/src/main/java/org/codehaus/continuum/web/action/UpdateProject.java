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
 * @version $Id: UpdateProject.java,v 1.2 2004-07-29 04:38:09 trygvis Exp $
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
