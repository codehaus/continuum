package org.codehaus.continuum.standalone;

/*
 * LICENSE
 */

import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.tool.hbm2ddl.SchemaExport;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: CreateDdl.java,v 1.1 2004-07-11 16:53:36 trygvis Exp $
 */
public class CreateDdl
{
    public static void main( String[] args )
        throws Exception
    {
        Configuration configuration = new Configuration();

        configuration.configure( "/hibernate.cfg.xml" );

        SchemaExport exporter = new SchemaExport( configuration );

        boolean script = true;

        boolean export = false;

        exporter.create( script, export );
    }
}
