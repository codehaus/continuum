package org.codehaus.continuum.web.pipeline.valve;

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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.continuum.web.action.Action;
import org.codehaus.continuum.web.action.ActionManager;
import org.codehaus.plexus.summit.SummitConstants;
import org.codehaus.plexus.summit.exception.SummitException;
import org.codehaus.plexus.summit.pipeline.valve.AbstractValve;
import org.codehaus.plexus.summit.rundata.RunData;
import org.codehaus.plexus.summit.view.ViewContext;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ActionValve.java,v 1.2 2005-02-21 15:03:16 trygvis Exp $
 */
public class ActionValve
    extends AbstractValve
{
    private ActionManager actionManager;

    public void invoke( RunData data )
        throws IOException, SummitException
    {
        String actionId = data.getRequest().getParameter( "action" );

        if ( actionId != null )
        {
            Action action = null;

            Map request = buildRequest( data );

            try
            {
                action = actionManager.lookupAction( actionId.trim() );
            }
            catch ( Exception ex )
            {
                handleException( request, actionId, ex );
            }

            try
            {
                action.execute( request );
            }
            catch ( Exception ex )
            {
                handleException( request, actionId, ex );
            }
        }
    }

    private Map buildRequest( RunData data )
    {
        Map request = new HashMap();

        // The parameter map in the request consists of an array of values for
        // the given key so this is why this is being done.
        for ( Enumeration e = data.getRequest().getParameterNames(); e.hasMoreElements(); )
        {
            String key = (String) e.nextElement();

            request.put( key, data.getRequest().getParameter( key ) );
        }

        request.put( "data", data );

        return request;
    }

    private void handleException( Map request, String actionId, Exception ex )
    {
        RunData data = (RunData) request.get( "data" );

        ViewContext vc = (ViewContext) data.getMap().get( SummitConstants.VIEW_CONTEXT );

        vc.put( "actionId", actionId );

        vc.put( "exceptionMessage", ex.getMessage() );

        StringWriter string = new StringWriter();

        ex.printStackTrace( new PrintWriter( string ) );

        vc.put( "exceptionStackTrace", string.toString() );

        data.setTarget( "Error.vm" );

        handleException2( actionId, ex );
    }

    private void handleException2( String actionId, Exception ex )
    {
        Throwable t = ex;

        StringWriter msg = new StringWriter();

        PrintWriter output = new PrintWriter( msg );

        output.println();
        output.println( "-------------------------------------------------------------------------------" );
        output.println( "Exception while executing the action: '" + actionId + "'." );

        while ( t != null )
        {
            ex.printStackTrace( output );

            t = t.getCause();
        }

        output.println( "-------------------------------------------------------------------------------" );
        output.println();

        output.flush();

        getLogger().error( msg.toString() );
    }
}
