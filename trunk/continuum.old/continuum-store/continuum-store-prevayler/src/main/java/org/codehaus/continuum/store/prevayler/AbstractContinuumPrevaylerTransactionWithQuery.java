package org.codehaus.continuum.store.prevayler;

/*
 * LICENSE
 */

import java.util.Date;

import org.codehaus.continuum.store.memory.ContinuumDatabase;

import org.prevayler.TransactionWithQuery;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AbstractContinuumPrevaylerTransactionWithQuery.java,v 1.2 2004-10-08 13:51:07 trygvis Exp $
 */
public abstract class AbstractContinuumPrevaylerTransactionWithQuery
    implements TransactionWithQuery
{
    protected abstract Object execute( ContinuumDatabase database )
        throws Exception;

    public Object executeAndQuery( Object prevalentSystem, Date executionTime )
    {
        try
        {
            return execute( (ContinuumDatabase) prevalentSystem );
        }
        catch( Exception ex )
        {
            throw new RuntimeException( "Error while executing transaction.", ex );
        }
    }
}
