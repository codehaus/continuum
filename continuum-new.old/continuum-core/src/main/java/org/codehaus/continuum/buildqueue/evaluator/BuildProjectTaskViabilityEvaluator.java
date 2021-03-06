package org.codehaus.continuum.buildqueue.evaluator;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.continuum.buildqueue.BuildProjectTask;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.taskqueue.TaskQueueException;
import org.codehaus.plexus.taskqueue.TaskViabilityEvaluator;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: BuildProjectTaskViabilityEvaluator.java,v 1.3 2005-03-10 00:05:51 trygvis Exp $
 */
public class BuildProjectTaskViabilityEvaluator
    extends AbstractLogEnabled
    implements TaskViabilityEvaluator
{
    /** @configuration */
    private long requiredBuildInterval;

    // ----------------------------------------------------------------------
    // TaskViabilityEvaluator Implementation
    // ----------------------------------------------------------------------

    public List evaluate( List tasks )
        throws TaskQueueException
    {
        // ----------------------------------------------------------------------
        // This code makes a Map with Lists with one list per project. For each
        // task in the list it puts it in the list for the project that's
        // requested for a build. Then all each of the lists with tasks is
        // checked for validity and a list of tasks to remove is returned.
        // ----------------------------------------------------------------------

        Map projects = new HashMap();

        for ( Iterator it = tasks.iterator(); it.hasNext(); )
        {
            BuildProjectTask task = (BuildProjectTask) it.next();

            List projectTasks = (List) projects.get( task.getProjectId() );

            if ( projectTasks == null )
            {
                projectTasks = new ArrayList();

                projects.put( task.getProjectId(), projectTasks );
            }

            projectTasks.add( task );
        }

        List toBeRemoved = new ArrayList();

        for ( Iterator it = projects.values().iterator(); it.hasNext(); )
        {
            toBeRemoved.addAll( checkTasks( (List) it.next() ) );
        }

        return toBeRemoved;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private List checkTasks( List list )
    {
        BuildProjectTask okTask = null;

        List toBeRemoved = new ArrayList();

        for ( Iterator it = list.iterator(); it.hasNext(); )
        {
            BuildProjectTask buildProjectTask = (BuildProjectTask) it.next();

            if ( okTask == null )
            {
                okTask = buildProjectTask;

                continue;
            }

            long interval = buildProjectTask.getTimestamp() - okTask.getTimestamp();

            if ( interval < requiredBuildInterval )
            {
                toBeRemoved.add( buildProjectTask );
            }
        }

        return toBeRemoved;
    }
}
