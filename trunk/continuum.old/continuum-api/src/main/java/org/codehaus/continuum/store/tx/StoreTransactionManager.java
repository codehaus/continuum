package org.codehaus.continuum.store.tx;

/*
 * LICENSE
 */

import org.codehaus.continuum.store.ContinuumStoreException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: StoreTransactionManager.java,v 1.1 2004-10-06 13:36:04 trygvis Exp $
 */
public interface StoreTransactionManager
{
    String ROLE = StoreTransactionManager.class.getName();

    void begin()
        throws ContinuumStoreException;

    void enter()
        throws ContinuumStoreException;

    void leave()
        throws ContinuumStoreException;

    void commit()
        throws ContinuumStoreException;

    void rollback();
}
