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

import java.util.Iterator;
import java.util.Map;

import org.codehaus.continuum.web.utils.WebUtils;
import org.codehaus.plexus.summit.SummitConstants;
import org.codehaus.plexus.summit.rundata.RunData;
import org.codehaus.plexus.summit.view.ViewContext;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AddProjectScm.java,v 1.3 2004-10-06 14:24:24 trygvis Exp $
 */
public class AddProjectScm
    extends AbstractAction
{
    public void execute( Map request )
        throws Exception
    {
//        try
//        {
            String name = (String) request.get( "addProject.name" );
    
            String scm = (String) request.get( "addProject.scm" );
    
            getContinuum().addProject( name, scm, "maven2" );
//        }
//        catch( ContinuumException ex )
//        {
//            handleError( request, "Error adding the project.", ex );
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
//        catch( Exception ex )
//        {
//            handleError( request, "Error while getting projects.", ex );
//
//            return;
//        }

        vc.put( "projects", WebUtils.projectsToProjectModels( getI18N(), projects ) );
    }
}
