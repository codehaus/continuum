package org.codehaus.continuum.trigger.alarmclock;

import java.util.Timer;
import java.util.TimerTask;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.trigger.AbstractContinuumTrigger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: AlarmClockTrigger.java,v 1.3 2004-05-19 22:19:13 trygvis Exp $
 */
public class AlarmClockTrigger
    extends AbstractContinuumTrigger
    implements Initializable, Startable
{
    private int interval;

    private int delay;

    private Timer timer;

    ///////////////////////////////////////////////////////////////////////////
    // Plexus Component Implementation

    public void initialize()
        throws Exception
    {
        if ( interval <= 0 )
            throw new ContinuumException( "Invalid value for 'interval': the interval must be bigger that 0." );

        if ( delay <= 0 )
            throw new ContinuumException( "Invalid value for 'delay': the delay must be bigger that 0." );

        if ( getContinuum() == null )
            throw new ContinuumException( "Missing requirement: 'continuum'." );

        timer = new Timer();
    }

    public void start()
        throws Exception
    {
        if ( interval == 1 )
            getLogger().info( "Starting AlarmClockTrigger with an interval of " + interval + " second" );
        else
            getLogger().info( "Starting AlarmClockTrigger with an interval of " + interval + " seconds" );

        timer.schedule( new BuildTask(), delay * 1000, interval * 1000 );
    }

    public void stop()
        throws Exception
    {
    }

    ///////////////////////////////////////////////////////////////////////////
    // Alarm Clock Trigger implementation

    private class BuildTask
        extends TimerTask
    {
        public void run()
        {
            try
            {
                getContinuum().buildProjects();
            }
            catch ( Exception e )
            {
                getLogger().error( "Can't build projects!", e );
            }
        }
    }
}
