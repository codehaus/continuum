package org.codehaus.continuum.store.memory.tx;

/*
 * LICENSE
 */

import org.codehaus.continuum.store.tx.AbstractStoreTransactionManager;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: MemoryStoreTransactionManager.java,v 1.1 2004-10-06 13:38:08 trygvis Exp $
 */
public class MemoryStoreTransactionManager
    extends AbstractStoreTransactionManager
{
    private static class MemoryStoreTransaction
    {
        private static int instanceCount = 0;

        private int instanceId = instanceCount++;

        public String toString()
        {
            return "MemoryStoreTransaction, id=" + instanceId;
        }
    }

    // ----------------------------------------------------------------------
    // StoreTransactionManager Implementation
    // ----------------------------------------------------------------------

    protected Object beginTransaction()
        throws Exception
    {
        return new MemoryStoreTransaction();
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
