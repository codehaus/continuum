package org.codehaus.continuum.web.utils;

/*
 * Copyright (c) 2004, Jason van Zyl and Trygve Laugstøl
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.builder.maven2.ExternalMaven2BuildResult;
import org.codehaus.continuum.builder.maven2.Maven2ProjectDescriptor;
import org.codehaus.continuum.builder.shell.ShellBuildResult;
import org.codehaus.continuum.builder.shell.ShellProjectDescriptor;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumBuildResult;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.web.model.BuildModel;
import org.codehaus.continuum.web.model.BuildResultModel;
import org.codehaus.continuum.web.model.ExternalMaven2BuildResultModel;
import org.codehaus.continuum.web.model.Maven2ProjectDescriptorModel;
import org.codehaus.continuum.web.model.ProjectDescriptorModel;
import org.codehaus.continuum.web.model.ProjectModel;
import org.codehaus.continuum.web.model.ShellBuildResultModel;
import org.codehaus.continuum.web.model.ShellProjectDescriptorModel;
import org.codehaus.plexus.i18n.I18N;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l </a>
 * @version $Id: WebUtils.java,v 1.7 2004-10-30 13:14:04 trygvis Exp $
 */
public class WebUtils
{
    private WebUtils()
    {
    }

    // ----------------------------------------------------------------------
    // Project
    // ----------------------------------------------------------------------

    public static List projectsToProjectModels( I18N i18n, Iterator projects )
    {
        List projectModels = new ArrayList();

        while ( projects.hasNext() )
        {
            ContinuumProject project = (ContinuumProject) projects.next();

            ProjectModel projectModel = projectToProjectModel( i18n, project );

            projectModels.add( projectModel );
        }

        return projectModels;
    }

    public static ProjectModel projectToProjectModel( I18N i18n, ContinuumProject project )
    {
        ProjectDescriptorModel projectDescriptorModel = null;

        if ( project.getType().equals( "maven2" ) )
        {
            Maven2ProjectDescriptor maven2ProjectDescriptor = (Maven2ProjectDescriptor) project.getDescriptor();

            if ( maven2ProjectDescriptor != null )
            {
                projectDescriptorModel = new Maven2ProjectDescriptorModel( maven2ProjectDescriptor.getGoals() );
            }
        }

        if ( project.getType().equals( "maven" ) )
        {
            ShellProjectDescriptor shellProjectDescriptor = (ShellProjectDescriptor) project.getDescriptor();

            if ( shellProjectDescriptor != null )
            {
                projectDescriptorModel = new ShellProjectDescriptorModel();
            }
        }

        ProjectModel projectModel = new ProjectModel( project.getId(),
            project.getName(),
            project.getScmUrl(),
            project.getNagEmailAddress(),
            project.getVersion(),
            project.getWorkingDirectory(),
            i18n.getString( project.getState().getI18nKey() ),
            project.getType(),
            projectDescriptorModel );

        return projectModel;
    }

    // ----------------------------------------------------------------------
    // Build
    // ----------------------------------------------------------------------

    public static List buildsToBuildModels( I18N i18n, Iterator builds )
        throws ContinuumException
    {
        List buildModels = new ArrayList();

        while ( builds.hasNext() )
        {
            ContinuumBuild build = (ContinuumBuild) builds.next();

            BuildModel buildModel = buildToBuildModel( i18n, build );

            buildModels.add( buildModel );
        }

        return buildModels;
    }

    public static BuildModel buildToBuildModel( I18N i18n, ContinuumBuild build )
        throws ContinuumException
    {
        String startDate = WebUtils.formatDate( build.getStartTime() );

        String endDate = WebUtils.formatDate( build.getEndTime() );

        String state = i18n.getString( build.getState().getI18nKey() );

        ContinuumBuildResult result = build.getBuildResult();

        BuildResultModel resultModel = null;

        if ( result instanceof ExternalMaven2BuildResult )
        {
            ExternalMaven2BuildResult externalMaven2BuildResult = (ExternalMaven2BuildResult) result;

            resultModel = new ExternalMaven2BuildResultModel( externalMaven2BuildResult.getStandardOutput(), externalMaven2BuildResult.getStandardError() );
        }
        else if ( result instanceof ShellBuildResult )
        {
            ShellBuildResult shellBuildResult = (ShellBuildResult) result;

            resultModel = new ShellBuildResultModel( shellBuildResult.getStandardOutput(), shellBuildResult.getStandardError() );
        }
        else if ( result == null )
        {
        }
        else
        {
            throw new ContinuumException( "Unknown build result type: " + result.getClass().getName() );
        }

        return new BuildModel( build.getId(), startDate, endDate, state, resultModel );
    }

    // ----------------------------------------------------------------------
    // Date formatters
    // ----------------------------------------------------------------------

    public static String formatDate( long date )
    {
        return formatDate( new Date( date ) );
    }

    public static String formatDate( Date date )
    {
        // TODO: Use the i18n component

        if ( date.getTime() == 0 )
        {
            return "";
        }

        SimpleDateFormat formatter = new SimpleDateFormat( "MM/dd/yyyy kk:mm:ss a" );

        return formatter.format( date );
    }
}
