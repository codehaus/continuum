package org.codehaus.continuum.it.it1;

/*
 * LICENSE
 */

import java.io.File;
import java.util.Iterator;

import org.codehaus.continuum.AbstractContinuumTest;
import org.codehaus.continuum.Continuum;
import org.codehaus.continuum.builder.maven2.ExternalMaven2ContinuumBuilder;
import org.codehaus.continuum.builder.maven2.Maven2ProjectDescriptor;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.store.ContinuumStore;
import org.codehaus.continuum.store.tx.StoreTransactionManager;
import org.codehaus.plexus.util.FileUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AbstractMaven2Test.java,v 1.2 2004-10-08 12:37:23 trygvis Exp $
 */
public abstract class AbstractMaven2Test
    extends AbstractContinuumTest
{
    private Class storeClass;

    public AbstractMaven2Test( Class storeClass )
    {
        this.storeClass = storeClass;
    }

    public abstract void setUpStore()
        throws Exception;

    public void tearDownStore()
        throws Exception
    {
    }

    public void setUp()
        throws Exception
    {
        super.setUp();

        // make sure the test is configured correctly

        assertEquals( storeClass, getContinuumStore().getClass() );

        assertEquals( ExternalMaven2ContinuumBuilder.class, getContinuumBuilder( "maven2" ).getClass() );

        setUpStore();
    }

    public void tearDown()
        throws Exception
    {
        tearDownStore();

        super.tearDown();
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void testUpdateProjectDescriptor()
        throws Exception
    {
        ContinuumStore store = getContinuumStore();

        StoreTransactionManager txManager = getStoreTransactionManager();

        Continuum continuum = getContinuum();

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

        txManager.begin();

        String projectName = "Hibernate And Maven 2 Test Project - Project - Original";

        String pomName = "Hibernate And Maven 2 Test Project - POM - Original";

        String projectScmConnection = "scm:test:target/repositories:hibernate-maven2";

        String projectId = continuum.addProject( projectName, projectScmConnection, "maven2" );

        ContinuumProject project = store.getProject( projectId );

        Maven2ProjectDescriptor descriptor = (Maven2ProjectDescriptor) project.getDescriptor();

        assertEquals( pomName, descriptor.getName() );

        // the pom should override the gived name
        assertEquals( pomName, project.getName() );

        projects = continuum.getAllProjects( 0, 0);

        assertTrue( projects.hasNext() );

        assertNotNull( projects.next() );

        assertFalse( projects.hasNext() );

        txManager.commit();

        // ----------------------------------------------------------------------
        // Update the project
        // ----------------------------------------------------------------------

        txManager.begin();

        FileUtils.copyFileToDirectory( new File( inRepo, "changed/pom.xml" ), outRepo );

        continuum.updateProject( projectId );

        project = continuum.getProject( projectId );

        descriptor = (Maven2ProjectDescriptor) project.getDescriptor();

        assertNotNull( descriptor );

        assertEquals( "Hibernate And Maven 2 Test Project - Changed", descriptor.getName() );

        txManager.commit();

        // ----------------------------------------------------------------------
        // Remove the project
        // ----------------------------------------------------------------------

        txManager.begin();

        continuum.removeProject( projectId );

        projects = continuum.getAllProjects( 0, 0);

        assertFalse( projects.hasNext() );

        txManager.commit();
    }

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
