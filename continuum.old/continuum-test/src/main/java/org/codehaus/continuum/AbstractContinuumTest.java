package org.codehaus.continuum;

/*
 * LICENSE
 */

import org.codehaus.continuum.builder.ContinuumBuilder;
import org.codehaus.continuum.buildqueue.BuildQueue;
import org.codehaus.continuum.notification.ContinuumNotifier;
import org.codehaus.continuum.scm.ContinuumScm;
import org.codehaus.continuum.store.ContinuumStore;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.context.Context;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l </a>
 * @version $Id: AbstractContinuumTest.java,v 1.2 2004-07-27 00:06:10 trygvis Exp $
 */
public abstract class AbstractContinuumTest
    extends PlexusTestCase
{
    protected void customizeContext()
    {
        Context context = getContainer().getContext();

        context.put( "maven.home", System.getProperty( "user.home" ) + "/.m2" );
    }

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
/*
    protected NotifierManager getNotifierManager()
        throws Exception
    {
        return (NotifierManager) lookupComponent( NotifierManager.ROLE );
    }
*/
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
/*
    protected MavenProjectBuilder getMavenProjectBuilder()
        throws Exception
    {
        return (MavenProjectBuilder) lookupComponent( MavenProjectBuilder.ROLE );
    }

    protected Maven getMaven()
        throws Exception
    {
        return (Maven) lookupComponent( Maven.ROLE );
    }

    protected String getLocalRepository()
        throws Exception
    {
        return getMaven().getLocalRepository();
    }
*/
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
