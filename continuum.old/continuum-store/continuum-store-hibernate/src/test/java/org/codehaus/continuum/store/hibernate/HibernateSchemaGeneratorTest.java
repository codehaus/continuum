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

import java.util.Properties;

import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.tool.hbm2ddl.SchemaExport;

import org.codehaus.plexus.ArtifactEnabledPlexusTestCase;
import org.codehaus.plexus.hibernate.DefaultHibernateService;
import org.codehaus.plexus.hibernate.HibernateService;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: HibernateSchemaGeneratorTest.java,v 1.5 2004-08-29 17:37:29 trygvis Exp $
 */
public class HibernateSchemaGeneratorTest
    extends ArtifactEnabledPlexusTestCase
{
    public void testCreateSchemas()
        throws Exception
    {
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
/* */
    }
}
