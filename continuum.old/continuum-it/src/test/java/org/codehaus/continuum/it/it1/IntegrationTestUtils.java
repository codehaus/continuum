package org.codehaus.continuum.it.it1;

/*
 * LICENSE
 */

import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.tool.hbm2ddl.SchemaExport;

import org.codehaus.continuum.TestUtils;
import org.codehaus.plexus.hibernate.DefaultHibernateService;
import org.codehaus.plexus.hibernate.HibernateService;
import org.codehaus.plexus.hibernate.HibernateSessionService;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: IntegrationTestUtils.java,v 1.1 2004-10-06 14:27:04 trygvis Exp $
 */
public class IntegrationTestUtils
    extends TestUtils
{
    public static void setUpHibernate()
        throws Exception
    {
        getContainer().lookup( HibernateSessionService.ROLE );

        DefaultHibernateService hibernate = (DefaultHibernateService) getContainer().lookup( HibernateService.ROLE );

        Configuration configuration = hibernate.getConfiguration();

        SchemaExport exporter = new SchemaExport( configuration );

        exporter.setDelimiter( ";" );

        exporter.create( false, true );
    }
}
