package org.codehaus.continuum.store.prevayler;

/*
 * LICENSE
 */

import org.codehaus.continuum.store.AbstractContinuumStoreTest;
import org.codehaus.continuum.store.ContinuumStore;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: PrevaylerContinuumStoreTest.java,v 1.1 2004-10-06 13:52:43 trygvis Exp $
 */
public class PrevaylerContinuumStoreTest
    extends AbstractContinuumStoreTest
{
    protected String getRoleHint()
    {
        return "prevayler";
    }

    public void setUp()
        throws Exception
    {
        super.setUp();

        ContinuumStore store = (ContinuumStore) lookup( ContinuumStore.ROLE, getRoleHint() );

        store.createDatabase();
    }

    public void tearDown()
        throws Exception
    {
        ContinuumStore store = (ContinuumStore) lookup( ContinuumStore.ROLE, getRoleHint() );

        store.deleteDatabase();

        super.tearDown();
    }
}
