package org.codehaus.continuum.store.prevayler;

/*
 * LICENSE
 */

import java.util.Date;

import org.codehaus.continuum.store.ContinuumStoreException;

import org.prevayler.Transaction;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AbstractContinuumPrevaylerTransaction.java,v 1.1 2004-10-06 13:52:43 trygvis Exp $
 */
public abstract class AbstractContinuumPrevaylerTransaction
    implements Transaction
{
    protected abstract void execute( ContinuumDatabase database )
        throws ContinuumStoreException;

    public void executeOn( Object prevalentSystem, Date executionTime )
    {
        try
        {
            execute( (ContinuumDatabase) prevalentSystem );
        }
        catch( ContinuumStoreException ex )
        {
            throw new RuntimeException( "Error while adding project.", ex );
        }
    }
}
