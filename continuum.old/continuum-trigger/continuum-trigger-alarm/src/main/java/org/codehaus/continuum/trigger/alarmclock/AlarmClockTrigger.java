package org.codehaus.continuum.trigger.alarmclock;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.trigger.AbstractContinuumTrigger;
import org.codehaus.continuum.utils.PlexusUtils;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: AlarmClockTrigger.java,v 1.6 2004-07-19 16:21:27 trygvis Exp $
 */
public class AlarmClockTrigger
    extends AbstractContinuumTrigger
    implements Initializable, Startable
{
    /**
     * The default value is one hour. 
     * 
     * @default 60000
     */
    private int interval;

    /**
     * The default value is 5 minutes.
     * 
     * @default 3000
     */
    private int delay;

    private Timer timer;

    protected Logger getLogger()
    {
        return super.getLogger();
    }

    // ----------------------------------------------------------------------
    // Plexus Component Implementation
    // ----------------------------------------------------------------------

    public void initialize()
        throws Exception
    {
        if ( interval <= 0 )
        {
            throw new ContinuumException( "Invalid value for 'interval': the interval must be bigger that 0." );
        }

        if ( delay <= 0 )
        {
            throw new ContinuumException( "Invalid value for 'delay': the delay must be bigger that 0." );
        }

        PlexusUtils.assertRequirement( getContinuum(), "continuum" );

        timer = new Timer();
    }

    public void start()
        throws Exception
    {
        getLogger().info( "Starting AlarmClockTrigger: Build interval " + interval + "s" );

        timer.schedule( new BuildTask(), delay * 1000, interval * 1000 );
    }

    public void stop()
    {
        timer.cancel();
    }

    // ----------------------------------------------------------------------
    // Alarm Clock Trigger implementation
    // ----------------------------------------------------------------------

    private class BuildTask
        extends TimerTask
    {
        public void run()
        {
            Iterator it;

            try
            {
                it = getContinuum().getAllProjects( 0, 0 );
            }
            catch ( Exception e )
            {
                getLogger().error( "Error while getting the project list.", e );

                return;
            }

            while ( it.hasNext() )
            {
                ContinuumProject project = (ContinuumProject) it.next();

                try
                {
                    getContinuum().buildProject( project.getId() );
                }
                catch( ContinuumException ex )
                {
                    getLogger().error( "Could not build project: " + project.getId() + " (" + project.getName() + ").", ex);

                    return;
                }
            }
        }
    }
}
