package org.codehaus.continuum.web.pipeline.valve;

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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.continuum.web.action.Action;
import org.codehaus.continuum.web.action.ActionManager;
import org.codehaus.continuum.store.ContinuumStore;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.hibernate.HibernateSessionService;
import org.codehaus.plexus.summit.exception.SummitException;
import org.codehaus.plexus.summit.pipeline.valve.AbstractValve;
import org.codehaus.plexus.summit.rundata.RunData;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ActionValve.java,v 1.2 2004-07-29 04:38:10 trygvis Exp $
 */
public class ActionValve
    extends AbstractValve
{
    //private ActionManager actionManager;

    public void invoke( RunData data )
        throws IOException, SummitException
    {
        String actionId = data.getRequest().getParameter( "action" );

        if ( actionId != null )
        {
            Action action = null;

            ContinuumStore store = null;

            PlexusContainer container = getServiceManager();

            try
            {
                ActionManager actionManager = (ActionManager) container.lookup( ActionManager.ROLE );

                store = (ContinuumStore) container.lookup( ContinuumStore.ROLE );

                HibernateSessionService hibernate = (HibernateSessionService) container.lookup( HibernateSessionService.ROLE );

                store.beginTransaction();

                action = actionManager.lookupAction( actionId.trim() );

                store.commitTransaction();

                hibernate.closeSession();
            }
            catch ( Exception ex )
            {
                try
                {
                    if ( store != null )
                    {
                        store.rollbackTransaction();
                    }
                }
                catch( Exception ex2 )
                {
                    // ignore
                }

                handleException( actionId, ex );
            }

            try
            {
                // The parameter map in the request consists of an array of values for
                // the given key so this is why this is being done.

                Map m = new HashMap();

                for ( Enumeration e = data.getRequest().getParameterNames(); e.hasMoreElements(); )
                {
                    String key = (String) e.nextElement();

                    m.put( key, data.getRequest().getParameter( key ) );
                }

                m.put( "data", data );

                action.execute( m );
            }
            catch ( Exception ex )
            {
                handleException( actionId, ex );
            }
        }
    }

    private void handleException( String actionId, Exception ex )
    {
        Throwable t = ex;

        StringWriter msg = new StringWriter();

        PrintWriter output = new PrintWriter( msg );

        output.println( "-------------------------------------------------------------------------------" );
        output.println( "Exception while executing the action: '" + actionId + "'." );

        while ( t != null )
        {
            ex.printStackTrace( output );

            t = t.getCause();
        }

        output.println( "-------------------------------------------------------------------------------" );

        output.flush();

        getLogger().error( msg.toString() );
    }
}
