package org.codehaus.continuum.web.utils;

/*
 * LICENSE
 */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.codehaus.continuum.builder.maven2.Maven2ProjectDescriptor;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.web.model.BuildModel;
import org.codehaus.continuum.web.model.Maven2ProjectDescriptorModel;
import org.codehaus.continuum.web.model.ProjectDescriptorModel;
import org.codehaus.continuum.web.model.ProjectModel;
import org.codehaus.plexus.i18n.I18N;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l </a>
 * @version $Id: WebUtils.java,v 1.1 2004-07-27 05:16:13 trygvis Exp $
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

            projectDescriptorModel = new Maven2ProjectDescriptorModel( maven2ProjectDescriptor.getGoals() );
        }

        ProjectModel projectModel = new ProjectModel( project.getId(),
            project.getName(),
            project.getScmConnection(),
            i18n.getString( project.getState().getI18nKey() ),
            project.getType(),
            projectDescriptorModel );

        return projectModel;
    }

    // ----------------------------------------------------------------------
    // Build
    // ----------------------------------------------------------------------

    public static List buildsToBuildModels( I18N i18n, Iterator builds )
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
    {
        String startDate = WebUtils.formatDate( build.getStartTime() );

        String endDate = WebUtils.formatDate( build.getEndTime() );

        String state = i18n.getString( build.getState().getI18nKey() );

        return new BuildModel( startDate, endDate, state );
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