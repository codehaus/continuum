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
import org.codehaus.continuum.web.tool.CssTool;
import org.codehaus.continuum.web.tool.OgnlTool;
import org.codehaus.plexus.i18n.I18N;
import org.codehaus.plexus.summit.pipeline.valve.CreateViewContextValve;
import org.codehaus.plexus.summit.rundata.RunData;
import org.codehaus.plexus.summit.view.ViewContext;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumViewContextValve.java,v 1.3 2005-03-10 00:05:57 trygvis Exp $
 */
public class ContinuumViewContextValve
    extends CreateViewContextValve
{
    private OgnlTool ognl = new OgnlTool();

    private CssTool css = new CssTool();

    private ContinuumWeb continuum;

    private I18N i18n;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    protected void populateViewContext( RunData data, ViewContext context )
    {
        context.put( "continuum", continuum );

        context.put( "css", css );

        context.put( "ognl", ognl );

        context.put( "i18n", i18n );

        context.put( "data", data );

        // We look at the view we are trying to present and we populate
        // context with the pertinent information.

        // I could use an ognl expression to extract the information
        // from the model to place in the

//        String view = data.getRequest().getParameter( "view" );

        /*

        I am just using the model directly here so this is not required for what I'm doing.

        If you were working with designers and wanted to make nice template options then
        this is what would be used.

        if ( view != null )
        {
            try
            {
                Map parameters = new HashMap();

                for ( Enumeration e = data.getRequest().getParameterNames(); e.hasMoreElements(); )
                {
                    String key = (String) e.nextElement();

                    parameters.put( key, data.getRequest().getParameter( key ) );
                }

                ViewContextManager vcm = (ViewContextManager) getServiceManager().lookup( ViewContextManager.ROLE );

                Map scalars = vcm.getScalars( view, context.get( "docorama" ), parameters );

                for ( Iterator i = scalars.keySet().iterator(); i.hasNext(); )
                {
                    String key = (String) i.next();

                    context.put( key, scalars.get( key ) );
                }
            }
            catch ( Exception e )
            {
                getLogger().error( "Error inserting scalars into the view context.", e );
            }
        }
        */
    }
}
