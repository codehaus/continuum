package org.codehaus.continuum.store.hibernate;

/*
 * Copyright (c) 2004, Jason van Zyl and Trygve Laugstøl
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.tool.hbm2ddl.SchemaExport;

import org.apache.maven.MavenTestUtils;

import org.codehaus.continuum.store.AbstractContinuumStoreTest;
import org.codehaus.continuum.store.ContinuumStore;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.hibernate.DefaultHibernateService;
import org.codehaus.plexus.hibernate.HibernateService;
import org.codehaus.plexus.hibernate.HibernateSessionService;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: HibernateContinuumStoreTest.java,v 1.6 2004-08-29 17:37:29 trygvis Exp $
 */
public class HibernateContinuumStoreTest
    extends AbstractContinuumStoreTest
{
    private HibernateSessionService sessionService;

    private ContinuumStore store;

    private Session session;

    private Transaction tx;

    protected PlexusContainer getContainerInstance()
    {
        return MavenTestUtils.getContainerInstance();
    }

    protected void customizeContext()
        throws Exception
    {
        MavenTestUtils.customizeContext( getContainer(), getTestFile( "" ) );
    }

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
