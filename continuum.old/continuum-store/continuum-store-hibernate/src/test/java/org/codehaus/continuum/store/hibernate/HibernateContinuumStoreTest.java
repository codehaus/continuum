package org.codehaus.continuum.store.hibernate;

/*
 * LICENSE
 */

import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.tool.hbm2ddl.SchemaExport;

import org.codehaus.continuum.store.AbstractContinuumStoreTest;
import org.codehaus.continuum.store.ContinuumStore;
import org.codehaus.plexus.hibernate.DefaultHibernateService;
import org.codehaus.plexus.hibernate.HibernateService;
import org.codehaus.plexus.hibernate.HibernateSessionService;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: HibernateContinuumStoreTest.java,v 1.4 2004-07-27 00:06:08 trygvis Exp $
 */
public class HibernateContinuumStoreTest
    extends AbstractContinuumStoreTest
{
    private HibernateSessionService sessionService;

    private ContinuumStore store;

    private Session session;

    private Transaction tx;

    protected String getRoleHint()
    {
        return "hibernate";
    }

    protected void beginTx()
        throws Exception
    {
        session = sessionService.getSession();

        store.beginTransaction();
    }

    protected void commitTx()
        throws Exception
    {
        store.commitTransaction();

        sessionService.closeSession();
    }

    protected void rollbackTx()
        throws Exception
    {
        store.rollbackTransaction();

        sessionService.closeSession();
    }

    public void setUp()
        throws Exception
    {
        super.setUp();

        sessionService = (HibernateSessionService) lookup( HibernateSessionService.ROLE );

        DefaultHibernateService hibernate = (DefaultHibernateService) lookup( HibernateService.ROLE );

        Configuration configuration = hibernate.getConfiguration();

        SchemaExport exporter = new SchemaExport( configuration );

        exporter.setDelimiter( ";" );

        exporter.create( false, true );

        session = sessionService.getSession();

        store = (ContinuumStore) lookup( ContinuumStore.ROLE, getRoleHint() );
    }
}
