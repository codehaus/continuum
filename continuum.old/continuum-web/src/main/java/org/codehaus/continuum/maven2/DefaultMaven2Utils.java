package org.codehaus.continuum.maven2;

/*
 * LICENSE
 */

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.maven.model.CiManagement;
import org.apache.maven.model.Scm;
import org.apache.maven.project.MavenProject;

import org.codehaus.continuum.Continuum;
import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.builder.ContinuumBuilder;
import org.codehaus.continuum.builder.maven2.Maven2ContinuumBuilder;
import org.codehaus.continuum.store.ContinuumStoreException;
import org.codehaus.continuum.store.tx.StoreTransactionManager;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultMaven2Utils.java,v 1.1 2004-10-20 17:02:45 trygvis Exp $
 */
public class DefaultMaven2Utils
	extends AbstractLogEnabled
	implements Maven2Utils
{
    /** @requirement */
    private Continuum continuum;

    /** @requirement */
    private ContinuumBuilder builder;

    /** @requirement */
    private StoreTransactionManager txManager;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws Exception
    {
        if ( !( builder instanceof Maven2ContinuumBuilder ) )
        {
            throw new Exception( "The builder has to be a Maven2ContinuumBuilder." );
        }
    }

    // ----------------------------------------------------------------------
    // Maven2Utils Implementation
    // ----------------------------------------------------------------------

    public String addProjectFromUrl( URL url )
    	throws ContinuumException
    {
        try
        {
            String pom = IOUtil.toString( url.openStream() );

            File pomFile = File.createTempFile( "continuum-", "-pom-download" );

            FileUtils.fileWrite( pomFile.getAbsolutePath(), pom );

            MavenProject project = ((Maven2ContinuumBuilder) builder).getProject( pomFile );

            String name = getProjectName( project );

            String scmUrl = getProjectScmUrl( project );

            String nagEmailAddress = getProjectNagEmailAddress( project );

            String version = getProjectVersion( project );

            txManager.enter();

            String projectId = continuum.addProject( name, scmUrl, nagEmailAddress, version, "maven2" );

            txManager.leave();

            return projectId;
        }
        catch( ContinuumStoreException ex )
        {
            txManager.rollback();

            throw new ContinuumException( "Error while adding propject.", ex );
        }
        catch( IOException ex )
        {
            txManager.rollback();

            throw new ContinuumException( "IO exception while adding propject.", ex );
        }
    }

    private static String getProjectName( MavenProject project )
    {
        String name = project.getName();

        if ( StringUtils.isEmpty( name ) )
        {
            name = project.getGroupId() + ":" + project.getArtifactId();
        }

        return name;
    }

    private static String getProjectScmUrl( MavenProject project )
        throws ContinuumException
    {
        Scm scm = project.getScm();

        if ( scm == null )
        {
            throw new ContinuumException( "Missing Scm from the project descriptor." );
        }

        String url = scm.getConnection();

        if ( StringUtils.isEmpty( url ) )
        {
            throw new ContinuumException( "Missing anonymous scm connection url." );
        }

        return url;
    }

    private static String getProjectNagEmailAddress( MavenProject project )
        throws ContinuumException
    {
        CiManagement ciManagement = project.getCiManagement();

        if ( ciManagement == null )
        {
            throw new ContinuumException( "Missing CiManagement from the project descriptor." );
        }

        String nagEmailAddress = ciManagement.getNagEmailAddress();

        if ( StringUtils.isEmpty( nagEmailAddress ) )
        {
            throw new ContinuumException( "Missing nag email address from the continuous integration info." );
        }

        return nagEmailAddress;
    }

    private static String getProjectVersion( MavenProject project )
        throws ContinuumException
    {
        String version = project.getVersion();

        if ( StringUtils.isEmpty( version ) )
        {
            throw new ContinuumException( "Missing version from the project descriptor." );
        }

        return version;
    }
}
