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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.web.utils.WebUtils;
import org.codehaus.plexus.i18n.I18N;
import org.codehaus.plexus.summit.SummitConstants;
import org.codehaus.plexus.summit.rundata.RunData;
import org.codehaus.plexus.summit.view.ViewContext;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ShowProjectBuilds.java,v 1.1 2004-07-27 05:16:12 trygvis Exp $
 */
public class ShowProjectBuilds
    extends AbstractAction
{
    /** @requirement */
    private I18N i18n;

    public void execute( Map map )
        throws Exception
    {
        RunData data = (RunData) map.get( "data" );

        ViewContext vc = (ViewContext) data.getMap().get( SummitConstants.VIEW_CONTEXT );

        String id = (String) map.get( "id" );

        ContinuumProject project = getContinuumStore().getProject( id );

        vc.put( "project", project );

        Iterator builds = getContinuumStore().getBuildsForProject( id, 0, 0 );

        List buildModels = WebUtils.buildsToBuildModels( i18n, builds );

        vc.put( "builds", buildModels );
    }
}
