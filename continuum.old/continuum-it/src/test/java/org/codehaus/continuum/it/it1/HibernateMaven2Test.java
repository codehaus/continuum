package org.codehaus.continuum.it.it1;

/*
 * LICENSE
 */

import java.io.File;

import org.codehaus.continuum.store.hibernate.HibernateContinuumStore;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: HibernateMaven2Test.java,v 1.1 2004-10-06 14:27:04 trygvis Exp $
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
/*
    public void testUpdateProjectDescriptor()
        throws Exception
    {
        Continuum continuum = getContinuum();

        ContinuumStore store = getContinuumStore();

        StoreTransactionManager txManager = getStoreTransactionManager();

        File inRepo = getTestFile( "src/test/repositories/hibernate-maven2/" );

        File outRepo = getTestFile( "target/repositories/hibernate-maven2" );

        FileUtils.deleteDirectory( outRepo );

        assertTrue( outRepo.mkdirs() );

        FileUtils.copyDirectoryStructure( new File( inRepo, "original" ), outRepo );

        // ----------------------------------------------------------------------
        // Sanity check
        // ----------------------------------------------------------------------

        txManager.begin();

        Iterator projects = continuum.getAllProjects( 0, 0);

        assertFalse( projects.hasNext() );

        txManager.commit();

        // ----------------------------------------------------------------------
        // Add the project
        // ----------------------------------------------------------------------

        String projectId = continuum.addProject( "Hibernate Maven 2 Test Project", "scm:test:target/repositories:hibernate-maven2", "maven2" );

        ContinuumProject project = store.getProject( projectId );

        Maven2ProjectDescriptor descriptor = (Maven2ProjectDescriptor) project.getDescriptor();

        assertEquals( "Hibernate And Maven 2 Test Project - Original", descriptor.getName() );

        projects = continuum.getAllProjects( 0, 0);

        assertTrue( projects.hasNext() );

        assertNotNull( projects.next() );

        assertFalse( projects.hasNext() );

        // ----------------------------------------------------------------------
        // Update the project
        // ----------------------------------------------------------------------

        FileUtils.copyFileToDirectory( new File( inRepo, "changed/pom.xml" ), outRepo );

        continuum.updateProject( projectId );

        project = continuum.getProject( projectId );

        descriptor = (Maven2ProjectDescriptor) project.getDescriptor();

        assertNotNull( descriptor );

        assertEquals( "Hibernate And Maven 2 Test Project - Changed", descriptor.getName() );

        // ----------------------------------------------------------------------
        // Remove the project
        // ----------------------------------------------------------------------

        continuum.removeProject( projectId );

        projects = continuum.getAllProjects( 0, 0);

        assertFalse( projects.hasNext() );
    }
*/
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
