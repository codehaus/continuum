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
 * @version $Id: EnqueueProject.java,v 1.2 2005-03-10 00:05:57 trygvis Exp $
 */
public class EnqueueProject
    extends AbstractAction
{
    public void execute( Map map )
        throws Exception
    {
        RunData data = (RunData) map.get( "data" );

        String id = (String) map.get( "id" );

        ViewContext vc = (ViewContext) data.getMap().get( SummitConstants.VIEW_CONTEXT );

        String buildId = getContinuum().buildProject( id );

        vc.put( "buildId", buildId );

        ContinuumProject project = getContinuumStore().getProject( id );

        vc.put( "project", project );
    }
}
