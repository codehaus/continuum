package org.codehaus.continuum.trigger.alarmclock;

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

import org.codehaus.continuum.Continuum;
import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.trigger.AbstractContinuumTrigger;
import org.codehaus.continuum.utils.PlexusUtils;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: AlarmClockTrigger.java,v 1.1.1.1 2005-02-17 22:23:54 trygvis Exp $
 */
public class AlarmClockTrigger
    extends AbstractContinuumTrigger
    implements Initializable, Startable
{
    private int interval;

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

        PlexusUtils.assertRequirement( getContinuum(), Continuum.ROLE );

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
                catch ( ContinuumException ex )
                {
                    getLogger().error( "Could not build project: " + project.getId() + " (" + project.getName() + ").", ex );

                    continue;
                }
            }
        }
    }
}
