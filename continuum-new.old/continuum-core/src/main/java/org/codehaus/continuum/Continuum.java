package org.codehaus.continuum;

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

import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.store.ContinuumStoreException;

/**
 * This is the main entry point for Continuum. Projects are added to Continuum
 * by providing an URL to the metadata for project. The metadata for a project
 * must contain the following information:
 * <p/>
 * o project name
 * o project id
 * o SCM information
 * o email notification list
 * o project developers
 */
public interface Continuum
{
    String ROLE = Continuum.class.getName();

    String addProjectFromUrl( URL url, String builder )
        throws ContinuumException;

    String addProjectFromScm( String scmUrl, String builderType, String projectName, String nagEmailAddress,
                              String version, Properties configuration )
        throws ContinuumException;

    void removeProject( String projectId )
        throws ContinuumException;

    void updateProjectFromScm( String projectId )
        throws ContinuumException;

    void updateProjectConfiguration( String projectId, Properties configuration )
        throws ContinuumException;

    ContinuumProject getProject( String projectId )
        throws ContinuumException;

    Iterator getAllProjects( int start, int end )
        throws ContinuumException;

    String buildProject( String projectId )
        throws ContinuumException;

    boolean checkIfProjectNeedsToBeBuilt( String projectId )
        throws ContinuumException;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    Iterator getProjects()
        throws ContinuumStoreException;

    ContinuumBuild getLatestBuildForProject( String id )
        throws ContinuumStoreException;
}
