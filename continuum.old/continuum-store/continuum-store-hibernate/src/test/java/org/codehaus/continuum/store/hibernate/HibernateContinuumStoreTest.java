package org.codehaus.continuum.store.hibernate;

/*
 * LICENSE
 */

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
 * @version $Id: HibernateContinuumStoreTest.java,v 1.3 2004-07-19 16:39:33 trygvis Exp $
 */
public class HibernateContinuumStoreTest
    extends AbstractContinuumStoreTest
{
    private HibernateSessionService sessionService;

    private Transaction tx;

    protected String getRoleHint()
    {
        return "hibernate";
    }

    protected void beginTx()
        throws Exception
    {
        ContinuumStore store = (ContinuumStore) lookup( ContinuumStore.ROLE, getRoleHint() );

        store.beginTransaction();
    }

    protected void commitTx()
        throws Exception
    {
        ContinuumStore store = (ContinuumStore) lookup( ContinuumStore.ROLE, getRoleHint() );

        store.commitTransaction();
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
    }
}
