package org.codehaus.continuum.web.pipeline.valve;

import org.codehaus.continuum.web.ContinuumWeb;
import org.codehaus.continuum.web.context.ViewContextPopulator;
import org.codehaus.continuum.web.tool.CssTool;
import org.codehaus.continuum.web.tool.OgnlTool;
import org.codehaus.continuum.Continuum;
import org.codehaus.plexus.i18n.I18N;
import org.codehaus.plexus.summit.pipeline.valve.CreateViewContextValve;
import org.codehaus.plexus.summit.pipeline.valve.AbstractValve;
import org.codehaus.plexus.summit.rundata.RunData;
import org.codehaus.plexus.summit.view.ViewContext;
import org.codehaus.plexus.summit.exception.SummitException;
import org.codehaus.plexus.summit.SummitConstants;

import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;
import java.util.Iterator;
import java.io.IOException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumViewContextPopulatorValve.java,v 1.1 2005-03-20 07:19:13 jvanzyl Exp $
 */
public class ContinuumViewContextPopulatorValve
    extends AbstractValve
{
    private ViewContextPopulator viewContextPopulator;

    private Continuum model;

    public void invoke( RunData data )
        throws IOException, SummitException
    {
        ViewContext vc = (ViewContext) data.getMap().get( SummitConstants.VIEW_CONTEXT );

        populateViewContext( data, vc );
    }

    protected void populateViewContext( RunData data, ViewContext context )
    {
        if ( data.getTarget() != null )
        {
            String view = data.getTarget();

            view = view.substring( 0, view.indexOf( "." ) );

            System.out.println( "view = " + view );

            if ( view != null )
            {

                try
                {
                    // ----------------------------------------------------------------------
                    // We take the parameters from the request so that they can be used to
                    // parameterize the expressions used to extract values out of the
                    // application model.
                    // ----------------------------------------------------------------------

                    Map parameters = new HashMap();

                    for ( Enumeration e = data.getRequest().getParameterNames(); e.hasMoreElements(); )
                    {
                        String key = (String) e.nextElement();

                        parameters.put( key, data.getRequest().getParameter( key ) );
                    }

                    // ----------------------------------------------------------------------
                    // Now that we have the parameters we pass those in along with the
                    // application model to create the set of scalars that will be
                    // placed in the requested view.
                    // ----------------------------------------------------------------------

                    Map scalars = viewContextPopulator.getScalars( view, model, parameters );

                    context.putAll( scalars );
                }
                catch ( Exception e )
                {
                    getLogger().error( "Error inserting scalars into the view context.", e );
                }
            }
        }
    }
}
