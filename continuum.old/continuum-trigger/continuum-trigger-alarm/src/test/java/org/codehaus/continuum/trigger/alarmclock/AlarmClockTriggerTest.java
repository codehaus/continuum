package org.codehaus.continuum.trigger.alarmclock;

/*
 * LICENSE
 */

import org.codehaus.continuum.Continuum;
import org.codehaus.continuum.MockContinuum;
import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AlarmClockTriggerTest.java,v 1.1 2004-05-19 22:24:42 trygvis Exp $
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
        MockContinuum continuum = ( MockContinuum ) lookup( Continuum.ROLE );

        continuum.addProject( "g1", "a1", "url://1" );

        continuum.addProject( "g2", "a2", "url://2" );

        AlarmClockTrigger trigger = getAlarmClockTrigger( "normal" );

        assertEquals( 0, continuum.getBuildQueueLength() );

        Thread.sleep( 2000 );

        assertEquals( 2, continuum.getBuildQueueLength() );

        release( trigger );
    }

    private AlarmClockTrigger getAlarmClockTrigger( String hint )
        throws Exception
    {
        Object trigger = lookup( AlarmClockTrigger.ROLE, hint );

        assertNotNull( trigger );

        return ( AlarmClockTrigger )trigger;
    }
}
