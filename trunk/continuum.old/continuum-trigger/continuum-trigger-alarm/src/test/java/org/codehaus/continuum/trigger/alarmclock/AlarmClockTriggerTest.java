package org.codehaus.continuum.trigger.alarmclock;

/*
 * LICENSE
 */

import org.codehaus.continuum.Continuum;
import org.codehaus.continuum.builder.ContinuumBuilder;
import org.codehaus.continuum.builder.TestContinuumBuilder;
import org.codehaus.continuum.trigger.ContinuumTrigger;
import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AlarmClockTriggerTest.java,v 1.2 2004-07-14 05:30:56 trygvis Exp $
 */
public class AlarmClockTriggerTest
    extends PlexusTestCase
{
    /**
     * This test is pretty timing critical.
     */
    public void testAlarmClockTrigger()
        throws Exception
    {
        Continuum continuum = ( Continuum ) lookup( Continuum.ROLE );

        assertEquals( 0, continuum.getBuildQueueLength() );

        continuum.addProject( "Test Project 1", "scm:foo", "test" );

        continuum.addProject( "Test Project 2", "scm:foo", "test" );

        // The lookup starts the trigger
        AlarmClockTrigger trigger = (AlarmClockTrigger) lookup( ContinuumTrigger.ROLE, "timer-test" );

        // before any of the builds is triggered
        assertEquals( 0, continuum.getBuildQueueLength() );

        // The alarm goes of every second and the builder should 
        // build every 100th second
        Thread.sleep( 2000 );

        assertEquals( 0, continuum.getBuildQueueLength() );

        TestContinuumBuilder testContinuumBuilder = (TestContinuumBuilder) lookup( ContinuumBuilder.ROLE, "test" );

        assertEquals( 2, testContinuumBuilder.getBuildCount() );

        release( trigger );
    }

    public void testConfigurationValues()
    {
        assertException( "zero-interval" );
        assertException( "negative-interval" );
        assertException( "string-interval" );

        assertException( "zero-delay" );
        assertException( "negative-delay" );
        assertException( "string-delay" );
    }

    private void assertException( String roleHint )
    {
        try
        {
            getAlarmClockTrigger( roleHint );

            fail( "Expected exception while looking up AlarmClockTrigger with hint '" + roleHint + "'." );
        }
        catch( Exception ex )
        {
            // expected
        }
    }

    private AlarmClockTrigger getAlarmClockTrigger( String roleHint )
        throws Exception
    {
        AlarmClockTrigger trigger = (AlarmClockTrigger) lookup( AlarmClockTrigger.ROLE, roleHint );

        assertNotNull( trigger );

        return trigger;
    }
}
