package org.codehaus.continuum.builder;

/*
 * LICENSE
 */

import java.util.Iterator;
import java.util.Map;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.project.BuildResult;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.store.ContinuumStore;
import org.codehaus.continuum.store.ContinuumStoreException;
import org.codehaus.continuum.utils.PlexusUtils;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultBuilderManager.java,v 1.1 2004-07-07 02:34:34 trygvis Exp $
 */
public class DefaultBuilderManager
    extends AbstractLogEnabled
    implements BuilderManager, Initializable
{
    /** */
    private ContinuumStore store;

    /** */
    private Map builders;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws Exception
    {
        PlexusUtils.assertRequirement( store, ContinuumStore.ROLE );

        if ( builders == null || builders.size() == 0 )
        {
            getLogger().warn( "No builders defined." );
        }
        else
        {
            for ( Iterator it = builders.keySet().iterator(); it.hasNext(); )
            {
                getLogger().info( "Found builder: " + it.next().toString() );
            }
        }
    }

    // ----------------------------------------------------------------------
    // BuilderManager Implementation
    // ----------------------------------------------------------------------

    public ContinuumBuilder getBuilderForProject( String projectId )
        throws ContinuumException
    {
        try
        {
            ContinuumProject project = store.getProject( projectId );
    
            String type = project.getType();
    
            ContinuumBuilder builder = (ContinuumBuilder) builders.get( type );
    
            if ( builder == null )
            {
                throw new ContinuumException( "No such builder: '" + type + "'." );
            }
    
            return builder;
        }
        catch( ContinuumStoreException ex )
        {
            throw new ContinuumException( "Error while getting build result.", ex );
        }
    }

    public ContinuumBuilder getBuilderForBuild( String buildId )
        throws ContinuumException
    {
        try
        {
            BuildResult build = store.getBuildResult( buildId );
    
            String type = build.getProject().getType();
    
            ContinuumBuilder builder = (ContinuumBuilder) builders.get( type );
    
            if ( builder == null )
            {
                throw new ContinuumException( "No such builder: '" + type + "'." );
            }
    
            return builder;
        }
        catch( ContinuumStoreException ex )
        {
            throw new ContinuumException( "Error while getting build result.", ex );
        }
    }
}
