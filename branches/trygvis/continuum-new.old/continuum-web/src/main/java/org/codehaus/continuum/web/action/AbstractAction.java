package org.codehaus.continuum.web.action;

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
 * @version $Id: AbstractAction.java,v 1.1.1.1 2005-02-17 22:23:56 trygvis Exp $
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
