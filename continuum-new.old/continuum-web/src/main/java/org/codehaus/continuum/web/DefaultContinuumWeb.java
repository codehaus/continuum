package org.codehaus.continuum.web;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Properties;

import org.codehaus.continuum.network.ConnectionFactory;
import org.codehaus.continuum.store.ContinuumStore;
import org.codehaus.continuum.trigger.ContinuumTrigger;
import org.codehaus.continuum.utils.PlexusUtils;
import org.codehaus.continuum.web.action.ActionManager;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultContinuumWeb.java,v 1.3 2005-03-15 07:37:37 trygvis Exp $
 */
public class DefaultContinuumWeb
    extends AbstractLogEnabled
    implements ContinuumWeb, Initializable, Contextualizable, Startable
{
    private ActionManager actionManager;

    private List triggers;

    private List connectionFactories;

    private ContinuumStore store;

    private PlexusContainer container;

    private String appHome;

    private final static String DATABASE_INITIALIZED = "database.initialized";

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void contextualize( Context context )
        throws Exception
    {
        container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }

    public void initialize()
        throws Exception
    {
    }

    public void start()
        throws Exception
    {
        PlexusUtils.assertConfiguration( appHome, "app-home" );

        store = (ContinuumStore) container.lookup( ContinuumStore.ROLE );

        // check to see if the tables exists or not.
        File file = new File( appHome, "continuum.properties" );

        Properties properties = new Properties();

        if ( !file.exists() )
        {
            initializeStore( file );
        }
        else
        {
            properties.load( new FileInputStream( file ) );

            String state = properties.getProperty( DATABASE_INITIALIZED );

            if ( !state.equals( "true" ) )
            {
                initializeStore( file );
            }
        }

        actionManager = (ActionManager) container.lookup( ActionManager.ROLE );

        triggers = container.lookupList( ContinuumTrigger.ROLE );

        connectionFactories = container.lookupList( ConnectionFactory.ROLE );
    }

    public void stop()
        throws Exception
    {
        if ( false )
        {
            container.release( actionManager );

            container.release( store );

            container.release( triggers );

            container.release( connectionFactories );
        }
    }

    private void initializeStore( File file )
        throws Exception
    {
        Properties properties = new Properties();

        getLogger().warn( "This system isn't configured. Configuring." );

        store.createDatabase();

        properties.setProperty( DATABASE_INITIALIZED, "true" );

        properties.store( new FileOutputStream( file ), null );
    }

    // ----------------------------------------------------------------------
    // ContinuumWeb Implementation
    // ----------------------------------------------------------------------

}
