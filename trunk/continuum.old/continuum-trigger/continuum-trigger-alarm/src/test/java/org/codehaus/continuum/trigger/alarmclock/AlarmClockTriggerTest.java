package org.codehaus.continuum.trigger.alarmclock;

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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.codehaus.continuum.AbstractContinuumTest;
import org.codehaus.continuum.Continuum;
import org.codehaus.continuum.builder.test.TestContinuumBuilder;
import org.codehaus.continuum.trigger.ContinuumTrigger;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AlarmClockTriggerTest.java,v 1.7 2004-10-06 14:18:59 trygvis Exp $
 */
public class AlarmClockTriggerTest
    extends AbstractContinuumTest
{
    /**
     * This test is pretty timing critical so if it fails try to increase the delays.
     */
    public void testAlarmClockTrigger()
        throws Exception
    {
        Continuum continuum = ( Continuum ) lookup( Continuum.ROLE );

        assertEquals( 0, continuum.getBuildQueueLength() );

        TestContinuumBuilder testContinuumBuilder = (TestContinuumBuilder) getContinuumBuilder( "test" );

        getStoreTransactionManager().begin();

        continuum.addProject( "Test Project 1", "scm:test:src/test/repository:simple", "test" );

        continuum.addProject( "Test Project 2", "scm:test:src/test/repository:simple", "test" );

        getStoreTransactionManager().commit();

        // The lookup starts the trigger
        AlarmClockTrigger trigger = (AlarmClockTrigger) getContinuumTrigger( "alarm-clock-test" );

        // before any of the builds is triggered
        assertEquals( 0, continuum.getBuildQueueLength() );

        // The alarm goes of every second and the builder should
        // build every 100th second
        Thread.sleep( 2000 );

        assertEquals( 0, continuum.getBuildQueueLength() );

        assertEquals( 2, testContinuumBuilder.getBuildCount() );

        release( trigger );
    }

    public void testConfigurationValues()
    {
        PrintStream out = System.out;

        System.setOut( new PrintStream( new ByteArrayOutputStream() ) );

        assertException( "zero-interval" );
        assertException( "negative-interval" );
        assertException( "string-interval" );

        assertException( "zero-delay" );
        assertException( "negative-delay" );
        assertException( "string-delay" );

        System.setOut( out );
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
        AlarmClockTrigger trigger = (AlarmClockTrigger) lookup( ContinuumTrigger.ROLE, roleHint );

        assertNotNull( trigger );

        return trigger;
    }
}
