package org.codehaus.continuum.store.hibernate;

/*
 * LICENSE
 */

import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.tool.hbm2ddl.SchemaExport;

import org.codehaus.plexus.hibernate.DefaultHibernateService;
import org.codehaus.plexus.hibernate.HibernateService;
import org.codehaus.plexus.hibernate.HibernateSessionService;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: HibernateContinuumStoreTest.java,v 1.1 2004-07-07 02:34:38 trygvis Exp $
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

    protected void connect()
        throws Exception
    {
        Session session = sessionService.getSession();

        tx = session.beginTransaction();
    }

    protected void disconnect()
        throws Exception
    {
        sessionService.getSession().flush();

        tx.commit();

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

        exporter.create( true, true );
    }
}
