package org.apache.maven.continuum.store;

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
import java.util.Properties;

import org.apache.maven.continuum.project.ContinuumBuild;
import org.apache.maven.continuum.project.ContinuumBuildResult;
import org.apache.maven.continuum.project.ContinuumProject;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumStore.java,v 1.2 2005-04-03 21:31:33 trygvis Exp $
 */
public interface ContinuumStore
{
    String ROLE = ContinuumStore.class.getName();

    // ----------------------------------------------------------------------
    // Database methods
    // ----------------------------------------------------------------------

    void createDatabase()
        throws ContinuumStoreException;

    void deleteDatabase()
        throws ContinuumStoreException;

    // ----------------------------------------------------------------------
    // ContinuumProject
    // ----------------------------------------------------------------------

    String addProject( String name, String scmUrl, String nagEmailAddress, String version, String builderId, String workingDirectory, Properties properties )
        throws ContinuumStoreException;

    void removeProject( String projectId )
        throws ContinuumStoreException;

    void setWorkingDirectory( String projectId, String workingDirectory )
        throws ContinuumStoreException;

    void updateProject( String projectId, String name, String scmUrl, String nagEmailAddress, String version )
        throws ContinuumStoreException;

    void updateProjectConfiguration( String projectId, Properties configuration )
        throws ContinuumStoreException;

    Iterator getAllProjects()
        throws ContinuumStoreException;

    Iterator findProjectsByName( String nameSearchPattern )
        throws ContinuumStoreException;

    ContinuumProject getProject( String projectId )
        throws ContinuumStoreException;

    ContinuumProject getProjectByBuild( String buildId )
        throws ContinuumStoreException;

    // ----------------------------------------------------------------------
    // Build
    // ----------------------------------------------------------------------

    String createBuild( String projectId )
        throws ContinuumStoreException;

    void setBuildResult( String buildId, int state, ContinuumBuildResult result, Throwable error )
        throws ContinuumStoreException;

    ContinuumBuild getBuild( String buildId )
        throws ContinuumStoreException;

    ContinuumBuild getLatestBuildForProject( String projectId )
        throws ContinuumStoreException;

    Iterator getBuildsForProject( String projectId, int start, int end )
        throws ContinuumStoreException;

    ContinuumBuildResult getBuildResultForBuild( String buildId )
        throws ContinuumStoreException;
}
