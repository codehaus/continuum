package org.codehaus.continuum.store.hibernate;

/*
 * LICENSE
 */

import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: HibernateSchemaGeneratorTest.java,v 1.2 2004-07-19 16:39:33 trygvis Exp $
 */
public class HibernateSchemaGeneratorTest
    extends PlexusTestCase
{
    public void testCreateSchemas()
        throws Exception
    {
/*
        DefaultHibernateService hibernate = (DefaultHibernateService) lookup( HibernateService.ROLE );

        Configuration configuration = hibernate.getConfiguration();

        String[] dialects = new String[]{
            "net.sf.hibernate.dialect.PostgreSQLDialect"
        };

        for ( int i = 0; i < dialects.length; i++ )
        {
            String dialect = dialects[i];

            System.out.println( "" );
            System.out.println( "" );
            System.out.println( "Writing schema for " + dialect );
            System.out.println( "" );
            System.out.println( "" );

            Properties properties = new Properties();

            properties.setProperty( "hibernate.dialect", dialect );

            SchemaExport exporter = new SchemaExport( configuration, properties );

            exporter.setDelimiter( ";" );

            exporter.create( true, false );
        }
*/
    }
}
