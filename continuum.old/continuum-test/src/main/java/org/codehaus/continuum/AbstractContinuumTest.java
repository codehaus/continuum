package org.codehaus.continuum;

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

import org.codehaus.continuum.builder.ContinuumBuilder;
import org.codehaus.continuum.buildqueue.BuildQueue;
import org.codehaus.continuum.notification.ContinuumNotifier;
import org.codehaus.continuum.scm.ContinuumScm;
import org.codehaus.continuum.store.ContinuumStore;
import org.codehaus.plexus.ArtifactEnabledPlexusTestCase;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l </a>
 * @version $Id: AbstractContinuumTest.java,v 1.5 2004-09-07 16:22:19 trygvis Exp $
 */
public abstract class AbstractContinuumTest
    extends ArtifactEnabledPlexusTestCase
{
    protected Continuum getContinuum()
        throws Exception
    {
        return (Continuum) lookupComponent( Continuum.ROLE );
    }

    protected ContinuumBuilder getContinuumBuilder()
        throws Exception
    {
        return (ContinuumBuilder) lookupComponent( ContinuumBuilder.ROLE );
    }

    protected ContinuumStore getContinuumStore( String role )
        throws Exception
    {
        return (ContinuumStore) lookupComponent( ContinuumStore.ROLE, role );
    }

    protected BuildQueue getBuildQueue()
        throws Exception
    {
        return (BuildQueue) lookupComponent( BuildQueue.ROLE );
    }

    protected ContinuumNotifier getContinuumNotifier( String roleHint )
        throws Exception
    {
        return (ContinuumNotifier) lookupComponent( ContinuumNotifier.ROLE, roleHint );
    }

    protected ContinuumScm getContinuumScm()
        throws Exception
    {
        return (ContinuumScm) lookupComponent( ContinuumScm.ROLE );
    }

    protected ContinuumStore getContinuumStore()
        throws Exception
    {
        return (ContinuumStore) lookupComponent( ContinuumStore.ROLE );
    }

    private Object lookupComponent( String role )
        throws Exception
    {
        Object component = lookup( role );

        assertNotNull( "Missing component: role " + role + ".", component );

        return component;
    }

    private Object lookupComponent( String role, String roleHint )
        throws Exception
    {
        Object component = lookup( role, roleHint );

        assertNotNull( "Missing component: role: " + role + ", hint: " + roleHint + ".", component );

        return component;
    }
}
