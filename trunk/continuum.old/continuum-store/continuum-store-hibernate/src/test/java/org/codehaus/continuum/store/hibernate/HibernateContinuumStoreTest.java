package org.codehaus.continuum.store.hibernate;

/*
 * Copyright (c) 2004, Jason van Zyl and Trygve Laugst�l
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

import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.tool.hbm2ddl.SchemaExport;

import org.codehaus.continuum.store.AbstractContinuumStoreTest;
import org.codehaus.plexus.hibernate.DefaultHibernateService;
import org.codehaus.plexus.hibernate.HibernateService;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: HibernateContinuumStoreTest.java,v 1.8 2004-10-15 13:01:06 trygvis Exp $
 */
public class HibernateContinuumStoreTest
    extends AbstractContinuumStoreTest
{
    protected String getRoleHint()
    {
        return "hibernate";
    }

    public void setUp()
        throws Exception
    {
        super.setUp();

        // TODO: use store.createDatabase() ?

//        sessionService = (HibernateSessionService) lookup( HibernateSessionService.ROLE );

        DefaultHibernateService hibernate = (DefaultHibernateService) lookup( HibernateService.ROLE );

        Configuration configuration = hibernate.getConfiguration();

        SchemaExport exporter = new SchemaExport( configuration );

        exporter.setDelimiter( ";" );

        exporter.create( false, true );

//        session = sessionService.getSession();
    }
}
