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

import org.codehaus.continuum.Continuum;
import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.store.ContinuumStore;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.i18n.I18N;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AbstractAction.java,v 1.2 2005-03-10 00:05:57 trygvis Exp $
 */
public abstract class AbstractAction
    extends AbstractLogEnabled
    implements Contextualizable, Action
{
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

    protected I18N getI18N()
        throws ContinuumException
    {
        try
        {
            return (I18N) container.lookup( I18N.ROLE );
        }
        catch ( ComponentLookupException ex )
        {
            throw new ContinuumException( "Exception while looking up I18N.", ex );
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
}
