package org.codehaus.continuum.web.pipeline.valve;

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

import org.codehaus.continuum.web.ContinuumWeb;
import org.codehaus.plexus.i18n.I18N;
import org.codehaus.plexus.summit.pipeline.valve.CreateViewContextValve;
import org.codehaus.plexus.summit.rundata.RunData;
import org.codehaus.plexus.summit.view.ViewContext;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumViewContextValve.java,v 1.7 2005-03-20 19:37:23 jvanzyl Exp $
 */
public class ContinuumViewContextValve
    extends CreateViewContextValve
{
    private ContinuumWeb continuum;

    private I18N i18n;

    protected void populateViewContext( RunData data, ViewContext context )
    {
        context.put( "continuum", continuum );

        context.put( "i18n", i18n );

        context.put( "data", data );
    }
}
