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
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.plexus.summit.SummitConstants;
import org.codehaus.plexus.summit.rundata.RunData;
import org.codehaus.plexus.summit.view.ViewContext;

import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ShowProjectBuilds.java,v 1.4 2005-03-10 00:05:57 trygvis Exp $
 */
public class ShowProjectBuilds
    extends AbstractAction
{
    public void execute( Map map )
        throws Exception
    {
        RunData data = (RunData) map.get( "data" );

        ViewContext vc = (ViewContext) data.getMap().get( SummitConstants.VIEW_CONTEXT );

        String id = (String) map.get( "id" );

        ContinuumProject project = getContinuumStore().getProject( id );

        vc.put( "project", project );

        Iterator it = getContinuumStore().getBuildsForProject( id, 0, 0 );

        List builds = new ArrayList();

        while ( it.hasNext() )
        {
            ContinuumBuild build = (ContinuumBuild) it.next();

            builds.add( build );
        }

        vc.put( "builds", builds );
    }
}
