package org.codehaus.continuum.web.pipeline.valve;

/*
 * Copyright (c) 2004, Jason van Zyl and Trygve Laugst?l
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

import org.codehaus.continuum.web.ContinuumWeb;
import org.codehaus.continuum.web.tool.CssTool;
import org.codehaus.continuum.web.tool.OgnlTool;
import org.codehaus.plexus.i18n.I18N;
import org.codehaus.plexus.summit.pipeline.valve.CreateViewContextValve;
import org.codehaus.plexus.summit.rundata.RunData;
import org.codehaus.plexus.summit.view.ViewContext;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumViewContextValve.java,v 1.2 2005-02-21 15:03:16 trygvis Exp $
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
