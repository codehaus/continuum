package org.codehaus.continuum.trigger.alarmclock;

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

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import org.codehaus.continuum.Continuum;
import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.trigger.AbstractContinuumTrigger;
import org.codehaus.continuum.utils.PlexusUtils;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AlarmClockTrigger.java,v 1.3 2005-03-29 16:41:34 trygvis Exp $
 */
public class AlarmClockTrigger
    extends AbstractContinuumTrigger
    implements Initializable, Startable
{
    /** @configuration */
    private int interval;

    /** @configuration */
    private int delay;

    private Timer timer;

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
        getLogger().info( "Build interval: " + interval + "s" );
        getLogger().info( "Will schedule the first build in: " + delay + "s" );

        timer.schedule( new BuildTask(), delay * 1000, interval * 1000 );
    }

    public void stop()
    {
        timer.cancel();
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void onTimer()
    {
        Iterator it;

        getLogger().info( "Scheduling projects." );

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
                getLogger().error( "Could not enqueue project: " + project.getId() +
                                   " ('" + project.getName() + "').", ex );

                continue;
            }
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private class BuildTask
        extends TimerTask
    {
        public void run()
        {
            onTimer();
        }
    }
}
