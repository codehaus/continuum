package org.codehaus.continuum.xmlrpc;

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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.codehaus.continuum.Continuum;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.store.ContinuumStore;
import org.codehaus.plexus.logging.AbstractLogEnabled;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultContinuumXmlRpc.java,v 1.1.1.1 2005-03-20 22:59:13 trygvis Exp $
 */
public class DefaultContinuumXmlRpc
    extends AbstractLogEnabled
    implements ContinuumXmlRpc
{
    /** @requirement */
    private Continuum continuum;

    /** @requirement */
    private ContinuumStore store;

    /** @requirement */
    private XmlRpcHelper xmlRpcHelper;

    // ----------------------------------------------------------------------
    // ContinuumXmlRpc Implementation
    // ----------------------------------------------------------------------

    // ----------------------------------------------------------------------
    // Projects
    // ----------------------------------------------------------------------

    public String addProjectFromUrl( String url, String builderType )
        throws Exception
    {
        try
        {
            return continuum.addProjectFromUrl( new URL( url ), builderType );
        }
        catch ( Exception e )
        {
            getLogger().error( "ContinuumXmlRpc.addProjectFromUrl(): url: '" + url + "'.", e );

            throw e;
        }
    }

    public String addProjectFromUrl( String scmUrl, String builderType, String projectName, String nagEmailAddress,
                                     String version, Hashtable configuration )
        throws Exception
    {
        try
        {
            Properties configurationProperties = new Properties();

            for ( Iterator it = configuration.entrySet().iterator(); it.hasNext(); )
            {
                Map.Entry entry = (Map.Entry) it.next();

                configurationProperties.put( entry.getKey().toString(), entry.getValue().toString() );
            }

            return continuum.addProjectFromScm( scmUrl, builderType, projectName, nagEmailAddress, version,
                                                configurationProperties );
        }
        catch ( Exception e )
        {
            getLogger().error( "ContinuumXmlRpc.addProjectFromUrl().", e );

            throw e;
        }
    }

    public Hashtable getProject( String projectId )
        throws Exception
    {
        try
        {
            return xmlRpcHelper.objectToHashtable( store.getProject( projectId ) );
        }
        catch ( Exception e )
        {
            getLogger().error( "ContinuumXmlRpc.getProject(): project id: '" + projectId + "'.", e );

            throw e;
        }
    }

    public Vector getAllProjects()
        throws Exception
    {
        try
        {
            Vector projects = new Vector();

            for ( Iterator it = store.getAllProjects(); it.hasNext(); )
            {
                ContinuumProject project = (ContinuumProject) it.next();

                projects.add( projectToHashtable( project ) );
            }

            return projects;
        }
        catch ( Exception e )
        {
            getLogger().error( "ContinuumXmlRpc.getAllProjects().", e );

            throw e;
        }
    }

    // ----------------------------------------------------------------------
    // Builds
    // ----------------------------------------------------------------------

    public String buildProject( String projectId )
        throws Exception
    {
        try
        {
            return continuum.buildProject( projectId );
        }
        catch ( Exception e )
        {
            getLogger().error( "ContinuumXmlRpc.buildProject(): project id: '" + projectId + "'.", e );

            throw e;
        }
    }

    public Vector getBuildsForProject( String projectId, int start, int end )
        throws Exception
    {
        try
        {
            Iterator it = store.getBuildsForProject( projectId, start, end );

            Vector builds = new Vector();

            while ( it.hasNext() )
            {
                ContinuumBuild continuumBuild = (ContinuumBuild) it.next();

                builds.add( buildToHashtable( continuumBuild ) );
            }

            return builds;
        }
        catch ( Exception e )
        {
            getLogger().error( "ContinuumXmlRpc.getBuildsForProject(): id: '" + projectId + "'.", e );

            throw e;
        }
    }

    public Hashtable getBuild( String buildId )
        throws Exception
    {
        try
        {
            ContinuumBuild build = store.getBuild( buildId );

            return buildToHashtable( build );
        }
        catch ( Exception e )
        {
            getLogger().error( "ContinuumXmlRpc.getBuild(): id: '" + buildId + "'.", e );

            throw e;
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private Hashtable projectToHashtable( ContinuumProject continuumProject )
        throws Exception
    {
/*
        Hashtable project = new Hashtable();

        project.put( "id", continuumProject.getId() );

        project.put( "name", continuumProject.getName() );

        project.put( "scmUrl", continuumProject.getScmUrl() );

        project.put( "nagEmailAddress", continuumProject.getNagEmailAddress() );

        project.put( "version", continuumProject.getVersion() );

        project.put( "workingDirectory", continuumProject.getWorkingDirectory() );

        project.put( "state", Integer.toString( continuumProject.getState() ) );

        project.put( "builderId", continuumProject.getBuilderId() );

        return project;
*/
        return xmlRpcHelper.objectToHashtable( continuumProject );
    }

    private Hashtable buildToHashtable( ContinuumBuild continuumBuild )
        throws Exception
    {
/*
        Hashtable build = new Hashtable();

        build.put( "id", continuumBuild.getId() );

        build.put( "state", Integer.toString( continuumBuild.getState() ) );

        build.put( "startTime", Long.toString( continuumBuild.getStartTime() ) );

        build.put( "endTime", Long.toString( continuumBuild.getEndTime() ) );

        build.put( "error", StringUtils.clean( continuumBuild.getError() ) );

        build.put( "buildResult", buildResultToHashtable( continuumBuild.getBuildResult() ) );

        return build;
*/
        return xmlRpcHelper.objectToHashtable( continuumBuild );
    }
/*
    private Hashtable buildResultToHashtable( ContinuumBuildResult continuumBuildResult )
    {
        Hashtable buildResult = new Hashtable();

        if ( continuumBuildResult == null )
        {
            getLogger().warn( "Build result was null." );

            return buildResult;
        }

        buildResult.put( "success", Boolean.toString( continuumBuildResult.isSuccess() ) );

        if ( continuumBuildResult instanceof Maven1BuildResult )
        {
            Maven1BuildResult maven1BuildResult = (Maven1BuildResult) continuumBuildResult;

            buildResult.put( "standardOutput", maven1BuildResult.getStandardOutput() );

            buildResult.put( "standardError", maven1BuildResult.getStandardError() );

            buildResult.put( "exitCode", Integer.toString( maven1BuildResult.getExitCode() ) );
        }
        else if ( continuumBuildResult instanceof AntBuildResult )
        {
            AntBuildResult antBuildResult = (AntBuildResult) continuumBuildResult;

            buildResult.put( "standardOutput", antBuildResult.getStandardOutput() );

            buildResult.put( "standardError", antBuildResult.getStandardError() );

            buildResult.put( "exitCode", Integer.toString( antBuildResult.getExitCode() ) );
        }
        else if ( continuumBuildResult instanceof ShellBuildResult )
        {
            ShellBuildResult shellBuildResult = (ShellBuildResult) continuumBuildResult;

            buildResult.put( "standardOutput", shellBuildResult.getStandardOutput() );

            buildResult.put( "standardError", shellBuildResult.getStandardError() );

            buildResult.put( "exitCode", Integer.toString( shellBuildResult.getExitCode() ) );
        }
        else
        {
            getLogger().warn( "Unknown build result: '" + continuumBuildResult.getClass().getName() + "'." );
        }

        return buildResult;
    }
*/
}
