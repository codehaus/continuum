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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import org.codehaus.continuum.Continuum;
import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.web.ContinuumWeb;
import org.codehaus.continuum.store.ContinuumStore;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.summit.SummitConstants;
import org.codehaus.plexus.summit.rundata.RunData;
import org.codehaus.plexus.summit.view.ViewContext;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AbstractAction.java,v 1.1 2004-07-27 05:16:12 trygvis Exp $
 */
public abstract class AbstractAction
    extends AbstractLogEnabled
    implements Contextualizable, Action
{
    private ContinuumWeb app;

    private PlexusContainer container;

    public void contextualize( Context context )
        throws ContextException
    {
        container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }

    protected Continuum getContinuum()
        throws ContinuumException
    {
        try
        {
            return (Continuum)container.lookup( Continuum.ROLE );
        }
        catch ( ComponentLookupException ex )
        {
            throw new ContinuumException( "Exception while looking up Continuum.", ex );
        }
    }

    protected ContinuumStore getContinuumStore()
        throws ContinuumException
    {
        try
        {
            return (ContinuumStore)container.lookup( ContinuumStore.ROLE );
        }
        catch ( ComponentLookupException ex )
        {
            throw new ContinuumException( "Exception while looking up Continuum.", ex );
        }
    }

    protected void handleError( Map request, String message )
    {
        handleError( request, message, null );
    }

    protected void handleError( Map request, String message, Exception ex )
    {
        RunData data = (RunData) request.get( "data" );

        ViewContext vc = (ViewContext) data.getMap().get( SummitConstants.VIEW_CONTEXT );

        vc.put( "error", message );

        if ( ex != null )
        {
            StringWriter output = new StringWriter( 10000 );

            PrintWriter p = new PrintWriter( output );

            ex.printStackTrace( p );

            p.flush();

            vc.put( "exception", output.toString() );
        }
    }
}
