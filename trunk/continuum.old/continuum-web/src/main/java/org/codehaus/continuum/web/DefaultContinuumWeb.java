package org.codehaus.continuum.web;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

import java.util.Iterator;

import org.codehaus.continuum.web.action.ActionManager;
import org.codehaus.continuum.store.ContinuumStore;
import org.codehaus.continuum.store.ContinuumStoreException;
import org.codehaus.plexus.jetty.ServletContainer;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultContinuumWeb.java,v 1.1 2004-07-27 05:16:12 trygvis Exp $
 */
public class DefaultContinuumWeb
    extends AbstractLogEnabled
    implements ContinuumWeb, Initializable
{
    private ClassLoader classLoader;

    private ActionManager actionManager;

    private ServletContainer servletContainer;

    private ContinuumStore store;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws Exception
    {
    }

    // ----------------------------------------------------------------------
    // ContinuumWeb Implementation
    // ----------------------------------------------------------------------

    // TODO: better exception handling
    public Iterator getAllProjects()
        throws ContinuumStoreException
    {
        return store.getAllProjects();
    }
}
