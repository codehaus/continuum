package org.codehaus.continuum.builder.manager;

/*
 * The MIT License
 *
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

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.builder.ContinuumBuilder;
import org.codehaus.continuum.store.ContinuumStore;
import org.codehaus.continuum.store.ContinuumStoreException;
import org.codehaus.continuum.utils.PlexusUtils;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultBuilderManager.java,v 1.2 2005-02-21 14:58:09 trygvis Exp $
 */
public class DefaultBuilderManager
    extends AbstractLogEnabled
    implements org.codehaus.continuum.builder.manager.BuilderManager, Initializable
{
    private ContinuumStore store;

    private Map builders;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws Exception
    {
        PlexusUtils.assertRequirement( store, ContinuumStore.ROLE );

        if ( builders == null )
        {
            builders = new HashMap();
        }

        if ( builders.size() == 0 )
        {
            getLogger().warn( "No builders defined." );
        }
        else
        {
            getLogger().info( "Builders:" );

            for ( Iterator it = builders.keySet().iterator(); it.hasNext(); )
            {
                getLogger().info( "  " + it.next().toString() );
            }
        }
    }

    // ----------------------------------------------------------------------
    // BuilderManager Implementation
    // ----------------------------------------------------------------------

    public ContinuumBuilder getBuilder( String builderType )
        throws ContinuumException
    {
        ContinuumBuilder builder = (ContinuumBuilder) builders.get( builderType );

        if ( builder == null )
        {
            throw new ContinuumException( "No such builder: '" + builderType + "'." );
        }

        return builder;
    }

    public ContinuumBuilder getBuilderForProject( String projectId )
        throws ContinuumException
    {
        try
        {
            ContinuumProject project = store.getProject( projectId );

            String builderType = project.getBuilderId();

            return getBuilder( builderType );
        }
        catch ( ContinuumStoreException ex )
        {
            throw new ContinuumException( "Error while getting build result.", ex );
        }
    }

    public ContinuumBuilder getBuilderForBuild( String buildId )
        throws ContinuumException
    {
        try
        {
            ContinuumBuild build = store.getBuild( buildId );

            String builderType = build.getProject().getBuilderId();

            return getBuilder( builderType );
        }
        catch ( ContinuumStoreException ex )
        {
            throw new ContinuumException( "Error while getting build result.", ex );
        }
    }
}
