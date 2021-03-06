package org.codehaus.continuum.store;

/*
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

import java.util.Iterator;

import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumBuildResult;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ContinuumProjectState;
import org.codehaus.continuum.project.ProjectDescriptor;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumStore.java,v 1.9 2004-11-02 14:25:53 jvanzyl Exp $
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

    String addProject( String name, String scmConnection, String nagEmailAddress, String version, String type )
        throws ContinuumStoreException;

    void removeProject( String projectId )
        throws ContinuumStoreException;

    void setProjectDescriptor( String projectId, ProjectDescriptor descriptor )
        throws ContinuumStoreException;

    void updateProjectDescriptor( String projectId, ProjectDescriptor descriptor )
        throws ContinuumStoreException;

    void updateProject( String projectId, String name, String scmUrl, String nagEmailAddress, String version )
        throws ContinuumStoreException;

    void setWorkingDirectory( String projectId, String workingDirectory )
    	throws ContinuumStoreException;

    Iterator getAllProjects()
        throws ContinuumStoreException;

    Iterator findProjectsByName( String nameSearchPattern )
        throws ContinuumStoreException;

    ContinuumProject getProject( String projectId )
        throws ContinuumStoreException;

    // ----------------------------------------------------------------------
    // Build
    // ----------------------------------------------------------------------

    String createBuild( String projectId )
        throws ContinuumStoreException;

    void setBuildResult( String buildId, ContinuumProjectState state, ContinuumBuildResult result, Throwable error )
        throws ContinuumStoreException;

    ContinuumBuild getBuild( String buildId )
        throws ContinuumStoreException;

    ContinuumBuild getLatestBuildForProject( String projectId )
        throws ContinuumStoreException;

    Iterator getBuildsForProject( String projectId, int start, int end )
        throws ContinuumStoreException;
}
