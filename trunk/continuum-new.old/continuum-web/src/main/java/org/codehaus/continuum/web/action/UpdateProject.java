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

import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.plexus.summit.SummitConstants;
import org.codehaus.plexus.summit.rundata.RunData;
import org.codehaus.plexus.summit.view.ViewContext;

import java.util.Map;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: UpdateProject.java,v 1.1.1.1 2005-02-17 22:23:57 trygvis Exp $
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
