package org.codehaus.continuum.registration;

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

import org.codehaus.continuum.Continuum;
import org.codehaus.continuum.store.tx.StoreTransactionManager;
import org.codehaus.continuum.utils.PlexusUtils;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: AbstractContinuumRegistrar.java,v 1.3 2004-10-28 17:17:38 trygvis Exp $
 */
public abstract class AbstractContinuumRegistrar
    extends AbstractLogEnabled
    implements ContinuumRegistrar, Initializable
{
    /** @requirement */
    private Continuum continuum;

    /** @requirement */
    private StoreTransactionManager txManager;

    // ----------------------------------------------------------------------
    // 
    // ----------------------------------------------------------------------

    public void initialize()
    	throws Exception
    {
        PlexusUtils.assertRequirement( continuum, Continuum.ROLE );
        PlexusUtils.assertRequirement( txManager, StoreTransactionManager.ROLE );
    }

    // ----------------------------------------------------------------------
    // 
    // ----------------------------------------------------------------------

    public Continuum getContinuum()
    {
        return continuum;
    }

    public StoreTransactionManager getStoreTransactionManager()
    {
        return txManager;
    }
}
