package org.apache.maven.continuum.builder.manager;

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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.maven.continuum.ContinuumException;
import org.apache.maven.continuum.builder.ContinuumBuilder;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultBuilderManager.java,v 1.1.1.1 2005-03-29 20:42:00 trygvis Exp $
 */
public class DefaultBuilderManager
    extends AbstractLogEnabled
    implements BuilderManager, Initializable
{
    private Map builders;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws Exception
    {
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

    public boolean hasBuilder( String builderType )
    {
        return builders.containsKey( builderType );
    }
}
