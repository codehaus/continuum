package org.codehaus.continuum.store.tx;

/*
 * LICENSE
 */

import org.codehaus.continuum.store.ContinuumStoreException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AbstractStoreTransactionManager.java,v 1.3 2004-10-15 13:00:57 trygvis Exp $
 */
public abstract class AbstractStoreTransactionManager
    extends AbstractLogEnabled
    implements StoreTransactionManager, Initializable
{
    private ThreadLocal txState = new ThreadLocal();

    // ----------------------------------------------------------------------
    // Abstract methods
    // ----------------------------------------------------------------------

    protected abstract Object beginTransaction()
        throws Exception;

    protected abstract void commitTransaction( Object transaction )
        throws Exception;

    protected abstract void rollbackTransaction( Object transaction )
        throws Exception;

    protected abstract void resetImplementation();

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private static class TransactionState
    {
        public Object transaction;

        public int nestingLevel;

        public boolean rolledBack;

        public String threadName = Thread.currentThread().getName();
    }

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
    {
    }

    // ----------------------------------------------------------------------
    // StoreTransactionManager Implementation
    // ----------------------------------------------------------------------

    public void begin()
        throws ContinuumStoreException
    {
        TransactionState state = getState();

        if ( !state.rolledBack && state.transaction != null )
        {
            throw new ContinuumStoreException( "A transaction is already in progress." );
        }

        state = reset();

        try
        {
            state.transaction = beginTransaction();

            getLogger().debug( "Started tx: " + state.transaction + ". Thread '" + Thread.currentThread().getName() + "'." );
        }
        catch( Exception ex )
        {
            throw new ContinuumStoreException( "Exception while beginning transaction.", ex );
        }

        state.nestingLevel++;
    }

    public void enter()
        throws ContinuumStoreException
    {
        TransactionState state = getState();

        if ( state.rolledBack )
        {
            throw new ContinuumStoreException( "The current transaction is rolled back." );
        }

        if ( state.nestingLevel == 0 )
        {
            throw new ContinuumStoreException( "There is no current transaction." );
        }

//        getLogger().info( "Entering transaction '" + state.transaction + "'. Thread: '" + state.threadName + "'." );

        state.nestingLevel++;
    }

    public void leave()
        throws ContinuumStoreException
    {
        TransactionState state = getState();

        if ( state.rolledBack )
        {
            throw new ContinuumStoreException( "The current transaction is rolled back." );
        }

        if ( state.transaction == null )
        {
            throw new ContinuumStoreException( "leave() called but there is no currently active transaction." );
        }

        if ( state.nestingLevel == 0)
        {
            throw new ContinuumStoreException( "leave() called but commit() or rollback() should have been called, nesting=0." );
        }

//        getLogger().info( "Leaving transaction '" + state.transaction + "'. Thread: '" + state.threadName + "'." );

        state.nestingLevel--;
    }

    public void commit()
        throws ContinuumStoreException
    {
        TransactionState state = getState();

        if ( state.rolledBack )
        {
            throw new ContinuumStoreException( "The current transaction is rolled back." );
        }

        if ( state.transaction == null )
        {
            throw new ContinuumStoreException( "No transaction has been started." );
        }

        try
        {
            state.nestingLevel--;

            if ( state.nestingLevel != 0 )
            {
                throw new ContinuumStoreException( "commit() called but the transaction is not balanced. level=" + state.nestingLevel + "." );
            }

            Object tx = state.transaction;

            getLogger().debug( "Committing transaction " + tx + ". Thread '" + Thread.currentThread().getName() + "'." );

            commitTransaction( tx );
        }
        catch( Exception ex )
        {
            throw new ContinuumStoreException( "Exception while committing transaction.", ex );
        }
        finally
        {
            reset();
        }
    }

    public void rollback()
    {
        try
        {
            TransactionState state = getState();

            if ( state.transaction == null )
            {
                return;
//                getLogger().error( "No transaction has been started.", new Exception( "Stack trace" ) );
            }
/*
            state.nestingLevel--;

            if ( state.nestingLevel != 0 )
            {
                getLogger().error( "rollback() called but the transaction is not balanced. level=" + state.nestingLevel + ". Rolling back the transaction.", new Exception( "Stack trace" ) );
            }
*/
            state.rolledBack = true;

            rollbackTransaction( state.transaction );
        }
        catch( Exception ex )
        {
            getLogger().info( "Exception while rolling back transaction.", ex );
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private TransactionState getState()
        throws ContinuumStoreException
    {
        TransactionState state = (TransactionState) txState.get();

        if ( state == null )
        {
            state = resetState();
        }

        return state;
    }

    private TransactionState reset()
    {
        resetImplementation();

        return resetState();
    }

    private TransactionState resetState()
    {
//        getLogger().info( "resetting state, " + Thread.currentThread().getName(), new Exception() );

        TransactionState state = new TransactionState();

        txState.set( state );

        return state;
    }
}
