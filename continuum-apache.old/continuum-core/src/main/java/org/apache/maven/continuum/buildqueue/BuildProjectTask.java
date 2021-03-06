package org.apache.maven.continuum.buildqueue;

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

import org.codehaus.plexus.taskqueue.Task;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: BuildProjectTask.java,v 1.1.1.1 2005-03-29 20:42:01 trygvis Exp $
 */
public class BuildProjectTask
    implements Task
{
    private String projectId;

    private String buildId;

    private long timestamp;

    public BuildProjectTask( String projectId, String buildId, long timestamp )
    {
        this.projectId = projectId;

        this.buildId = buildId;

        this.timestamp = timestamp;
    }

    public String getProjectId()
    {
        return projectId;
    }

    public String getBuildId()
    {
        return buildId;
    }

    public long getTimestamp()
    {
        return timestamp;
    }
}
