package org.codehaus.continuum.store.hibernate.tx;

/*
 * LICENSE
 */

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;

import org.codehaus.continuum.store.ContinuumStoreException;
import org.codehaus.continuum.store.tx.AbstractStoreTransactionManager;
import org.codehaus.plexus.hibernate.HibernateSessionService;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: HibernateStoreTransactionManager.java,v 1.1 2004-10-06 13:48:09 trygvis Exp $
 */
public class HibernateStoreTransactionManager
    extends AbstractStoreTransactionManager
{
    /** @requirement */
    private HibernateSessionService hibernate;

    protected Object beginTransaction()
        throws Exception
    {
        Session session = getHibernateSession();

        Transaction tx = session.beginTransaction();

        getLogger().warn( "Started transaction: " + tx );

        return tx;
    }

    protected void commitTransaction( Object transaction )
        throws Exception
    {
        ((Transaction) transaction ).commit();
    }

    protected void rollbackTransaction( Object transaction )
        throws Exception
    {
        try
        {
            ((Transaction) transaction ).rollback();
        }
        finally
        {
            resetImplementation();
        }
    }

    protected void resetImplementation()
    {
        try
        {
            hibernate.closeSession();
        }
        catch( HibernateException ex )
        {
            getLogger().warn( "Exception while closing hibernate session.", ex );
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private Session getHibernateSession()
        throws ContinuumStoreException
    {
        try
        {
            return hibernate.getSession();
        }
        catch( HibernateException ex )
        {
            throw new ContinuumStoreException( "Exception while getting hibernate session.", ex );
        }
    }
}
