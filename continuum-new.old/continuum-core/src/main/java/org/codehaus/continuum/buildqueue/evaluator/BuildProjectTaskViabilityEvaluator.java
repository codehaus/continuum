package org.codehaus.continuum.buildqueue.evaluator;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
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
 * @version $Id: BuildProjectTaskViabilityEvaluator.java,v 1.2 2005-02-28 17:04:45 trygvis Exp $
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
