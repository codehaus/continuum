package org.codehaus.continuum;

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

import junit.framework.Assert;
import org.apache.maven.scm.manager.ScmManager;
import org.codehaus.continuum.builder.ContinuumBuilder;
import org.codehaus.continuum.buildqueue.BuildQueue;
import org.codehaus.continuum.scm.ContinuumScm;
import org.codehaus.continuum.store.ContinuumStore;
import org.codehaus.continuum.trigger.ContinuumTrigger;
import org.codehaus.plexus.ArtifactEnabledPlexusTestCase;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l </a>
 * @version $Id: AbstractContinuumTest.java,v 1.3 2005-03-10 00:05:26 trygvis Exp $
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
