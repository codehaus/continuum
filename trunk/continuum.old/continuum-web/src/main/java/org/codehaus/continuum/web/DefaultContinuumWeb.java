package org.codehaus.continuum.web;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import org.codehaus.continuum.store.ContinuumStore;
import org.codehaus.continuum.utils.PlexusUtils;
import org.codehaus.continuum.web.action.ActionManager;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.jetty.ServletContainer;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultContinuumWeb.java,v 1.2 2004-07-29 04:38:09 trygvis Exp $
 */
public class DefaultContinuumWeb
    extends AbstractLogEnabled
    implements ContinuumWeb, Initializable, Contextualizable, Startable
{
    private ActionManager actionManager;

    private ServletContainer servletContainer;

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

        servletContainer = (ServletContainer) container.lookup( ServletContainer.ROLE );
    }

    public void stop()
        throws Exception
    {
/*
        container.release( servletContainer );

        container.release( actionManager );

        container.release( store );
*/
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
