package org.codehaus.continuum.store.stash;

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

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.continuum.store.ContinuumStore;

import java.util.Iterator;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: StashContinuumStoreTest.java,v 1.2 2005-03-10 00:05:56 trygvis Exp $
 */
public class StashContinuumStoreTest
    extends PlexusTestCase
{
    public void testObieContinuumStore()
        throws Exception
    {
        ContinuumStore store = (ContinuumStore) lookup( ContinuumStore.ROLE, "stash" );

        // ----------------------------------------------------------------------
        // There are no projects entered yet so the iterator will be null.
        // ----------------------------------------------------------------------

        Iterator iterator = store.getAllProjects();
    }
}
