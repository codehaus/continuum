package org.codehaus.continuum.store.stash;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.continuum.store.ContinuumStore;

import java.util.Iterator;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: StashContinuumStoreTest.java,v 1.1.1.1 2005-02-17 22:23:54 trygvis Exp $
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
