package org.codehaus.continuum.store.prevayler.tx;

/*
 * LICENSE
 */

import org.codehaus.continuum.store.tx.AbstractStoreTransactionManager;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: PrevaylerStoreTransactionManager.java,v 1.1 2004-10-06 13:52:43 trygvis Exp $
 */
public class PrevaylerStoreTransactionManager
    extends AbstractStoreTransactionManager
{
    private static class PrevaylerStoreTransaction
    {
        private static int instanceCount = 0;

        private int instanceId = instanceCount++;

        public String toString()
        {
            return "PrevaylerStoreTransaction, id=" + instanceId;
        }
    }

    // ----------------------------------------------------------------------
    // StoreTransactionManager Implementation
    // ----------------------------------------------------------------------

    protected Object beginTransaction()
        throws Exception
    {
        return new PrevaylerStoreTransaction();
    }

    protected void commitTransaction( Object tx )
        throws Exception
    {
    }

    protected void rollbackTransaction( Object tx )
        throws Exception
    {
    }

    protected void resetImplementation()
    {
    }
}
