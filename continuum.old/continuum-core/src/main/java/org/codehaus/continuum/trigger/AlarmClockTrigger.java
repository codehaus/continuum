package org.codehaus.plexus.continuum.trigger;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: AlarmClockTrigger.java,v 1.1 2004-01-18 07:27:27 jvanzyl Exp $
 */
public class AlarmClockTrigger
    extends AbstractContinuumTrigger
    implements Initializable, Startable
{
    private int interval;

    private Timer timer;

    /** @see Initializable#initialize */
    public void initialize()
        throws Exception
    {
        timer = new Timer();
    }

    /** @see Startable#start */
    public void start()
        throws Exception
    {
        getLogger().info( "Starting AlarmClockTrigger with an interval of " + interval + " seconds" );

        timer.schedule( new BuildTask(), 0, interval * 60 * 1000 );
    }

    /** @see Startable#stop */
    public void stop()
        throws Exception
    {

    }

    class BuildTask
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
