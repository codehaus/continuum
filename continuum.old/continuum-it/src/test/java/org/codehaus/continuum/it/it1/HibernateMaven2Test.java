package org.codehaus.continuum.it.it1;

/*
 * LICENSE
 */

import java.io.File;

import org.codehaus.continuum.store.hibernate.HibernateContinuumStore;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: HibernateMaven2Test.java,v 1.2 2004-10-30 13:04:15 trygvis Exp $
 */
public class HibernateMaven2Test
    extends AbstractMaven2Test
{
    public HibernateMaven2Test()
    {
        super( HibernateContinuumStore.class );
    }

    public void setUpStore()
        throws Exception
    {
        tearDownStore();

        IntegrationTestUtils.setUpHibernate();
    }

    public void tearDownStore()
        throws Exception
    {
        deleteFile( "continuumdb.log" );

        deleteFile( "continuumdb.properties" );

        deleteFile( "continuumdb.sql" );
    }

    // ----------------------------------------------------------------------
    // 
    // ----------------------------------------------------------------------

    private void deleteFile( String fileName )
        throws Exception
    {
        File file = getTestFile( fileName );

        if ( file.exists() )
        {
            assertTrue( "Error while deleting " + file.getAbsolutePath(), file.delete() );
        }
    }
}
