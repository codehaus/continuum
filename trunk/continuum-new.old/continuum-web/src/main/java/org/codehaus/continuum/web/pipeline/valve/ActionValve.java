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
 * @version $Id: ActionValve.java,v 1.5 2005-03-20 07:19:13 jvanzyl Exp $
 */
public class ActionValve
    extends AbstractValve
{
    private ActionManager actionManager;

    public void invoke( RunData data )
        throws IOException, SummitException
    {
        String actionId = data.getRequest().getParameter( "action" );

        String target = data.getRequest().getParameter( "target" );

        System.out.println( "target = " + target );

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

                return;
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
