package org.codehaus.continuum;

/*
 * The MIT License
 *
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

import junit.framework.Assert;
import org.apache.maven.scm.manager.ScmManager;
import org.codehaus.continuum.builder.ContinuumBuilder;
import org.codehaus.continuum.buildqueue.BuildQueue;
import org.codehaus.continuum.notification.ContinuumNotifier;
import org.codehaus.continuum.scm.ContinuumScm;
import org.codehaus.continuum.store.ContinuumStore;
import org.codehaus.continuum.trigger.ContinuumTrigger;
import org.codehaus.plexus.ArtifactEnabledPlexusTestCase;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l </a>
 * @version $Id: AbstractContinuumTest.java,v 1.1.1.1 2005-02-17 22:23:48 trygvis Exp $
 */
public abstract class AbstractContinuumTest
    extends ArtifactEnabledPlexusTestCase
{
    private static AbstractContinuumTest instance;

    public void setUp()
        throws Exception
    {
        super.setUp();

        File plexusTemp = getTestFile( "target/plexus-home/temp" );

        // TODO: fix
//        plexusTemp = getTestFile( "${plexus.temp}" );

        FileUtils.deleteDirectory( plexusTemp );

        assertTrue( plexusTemp.mkdirs() );

        getContainer().getContext().put( "plexus.temp", plexusTemp.getAbsolutePath() );

        instance = this;
    }

    public static PlexusContainer getPlexusContainer()
    {
        assertNotNull( "super.setUp() must be called first.", instance );

        return instance.getContainer();
    }

    // ----------------------------------------------------------------------
    // Continuum lookups
    // ----------------------------------------------------------------------

    public static Continuum getContinuum()
        throws Exception
    {
        return (Continuum) lookupComponent( Continuum.ROLE );
    }

    public static ContinuumBuilder getContinuumBuilder( String type )
        throws Exception
    {
        return (ContinuumBuilder) lookupComponent( ContinuumBuilder.ROLE, type );
    }

    public static ContinuumStore getContinuumStore( String role )
        throws Exception
    {
        return (ContinuumStore) lookupComponent( ContinuumStore.ROLE, role );
    }

    public static BuildQueue getBuildQueue()
        throws Exception
    {
        return (BuildQueue) lookupComponent( BuildQueue.ROLE );
    }

    public static ContinuumNotifier getContinuumNotifier( String roleHint )
        throws Exception
    {
        return (ContinuumNotifier) lookupComponent( ContinuumNotifier.ROLE, roleHint );
    }

    public static ContinuumTrigger getContinuumTrigger( String roleHint )
        throws Exception
    {
        return (ContinuumTrigger) lookupComponent( ContinuumTrigger.ROLE, roleHint );
    }

    public static ContinuumScm getContinuumScm()
        throws Exception
    {
        return (ContinuumScm) lookupComponent( ContinuumScm.ROLE );
    }

    public static ContinuumStore getContinuumStore()
        throws Exception
    {
        return (ContinuumStore) lookupComponent( ContinuumStore.ROLE );
    }

    // ----------------------------------------------------------------------
    // Misc lookups
    // ----------------------------------------------------------------------

    public static ScmManager getScmManager()
        throws Exception
    {
        return (ScmManager) lookupComponent( ScmManager.ROLE );
    }

    // ----------------------------------------------------------------------
    // Generic lookups
    // ----------------------------------------------------------------------

    public static Object lookupComponent( String role )
        throws Exception
    {
        Object component = instance.lookup( role );

        Assert.assertNotNull( "Missing component: role " + role + ".", component );

        return component;
    }

    public static Object lookupComponent( String role, String roleHint )
        throws Exception
    {
        Object component = instance.lookup( role, roleHint );

        Assert.assertNotNull( "Missing component: role: " + role + ", hint: " + roleHint + ".", component );

        return component;
    }
}
