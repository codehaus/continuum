package org.codehaus.continuum.web.pipeline.valve;

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
 * @version $Id: ActionValve.java,v 1.1 2004-07-27 05:16:13 trygvis Exp $
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
