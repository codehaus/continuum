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
 * @version $Id: AbstractMaven2Test.java,v 1.5 2004-10-29 15:31:27 trygvis Exp $
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

        Iterator projects = continuum.getAllProjects( 0, 0 );

        assertFalse( projects.hasNext() );

        txManager.commit();

        // ----------------------------------------------------------------------
        // Add the project
        // ----------------------------------------------------------------------

        txManager.begin();

        String pomName = "Hibernate And Maven 2 Test Project - POM - Original";

        String projectScmUrl = "scm:local:target/repositories:hibernate-maven2";

        String projectId = continuum.addProjectFromScm( projectScmUrl, "maven2" );

        ContinuumProject project = store.getProject( projectId );

        Maven2ProjectDescriptor descriptor = (Maven2ProjectDescriptor) project.getDescriptor();

        // the pom should override the given name
        assertEquals( pomName, project.getName() );

        // TODO: add asserts for scmUrl, nagEmailAddress and version

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

        assertEquals( "Hibernate And Maven 2 Test Project - Changed", project.getName() );

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
/*
    public void testUpdatedOfProjectWhenUpdatingTheProjectDescriptorWithConnectionOnly()
        throws Exception
    {
        testUpdatedOfProjectWhenUpdatingTheProjectDescriptor( "overridden-connection", null );
    }

    public void testUpdatedOfProjectWhenUpdatingTheProjectDescriptorWithConnectionAndDeveloperConnection()
        throws Exception
    {
        testUpdatedOfProjectWhenUpdatingTheProjectDescriptor( "overridden-connection", "overridden-developer-connection" );
    }

    public void testUpdatedOfProjectWhenUpdatingTheProjectDescriptorWithDeveloperConnectionOnly()
        throws Exception
    {
        testUpdatedOfProjectWhenUpdatingTheProjectDescriptor( null, "overridden-developer-connection" );
    }

    private void testUpdatedOfProjectWhenUpdatingTheProjectDescriptor( String connection, String developerConnection )
        throws Exception
    {
        File scm = getTestFile( "target/scm-test" );

        FileUtils.deleteDirectory( scm );

        assertTrue( scm.mkdirs() );

        String pom = 
            "<project>" +
            "  <modelVersion>4.0.0</modelVersion>"+
            "  <groupId>continuum</groupId>" +
            "  <artifactId>test</artifactId>" +
            "  <version>test</version>" +
            "  <name>Pom Name</name>" +
            "  <ciManagement>" +
            "    <nagEmailAddress>foo@bar</nagEmailAddress>" +
            "  </ciManagement>" +
            "  <scm>" +
            (connection != null ?
            "    <connection>scm:local:target/scm-test:" + connection + "</connection>" : "" ) +
            (developerConnection != null ?
            "    <developerConnection>scm:local:target/scm-test:" + developerConnection + "</developerConnection>" : "" ) +
            "  </scm>" +
            "</project>";

        if ( connection != null )
        {
            assertTrue( new File( scm, connection ).mkdirs() );
        }

        if ( developerConnection != null )
        {
            assertTrue( new File( scm, developerConnection ).mkdirs() );
        }
        
        writePom( pom, scm, "pom.xml" );

        ContinuumStore store = getContinuumStore();

        StoreTransactionManager txManager = getStoreTransactionManager();

        Continuum continuum = getContinuum();

        txManager.begin();

        String scmUrl = "scm:local:target:scm-test";

        String projectId = continuum.addProjectFromScm( scmUrl, "maven2" );

        txManager.commit();

        txManager.begin();

        ContinuumProject project = store.getProject( projectId );

        assertEquals( "Pom Name", project.getName() );

        if ( connection != null && developerConnection != null )
        {
            assertEquals( connection, project.getScmUrl() );
        }
        else if ( connection != null && developerConnection == null )
        {
            assertEquals( connection, project.getScmUrl() );
        }
        else if ( connection == null && developerConnection != null )
        {
            assertEquals( developerConnection, project.getScmUrl() );
        }

        txManager.commit();
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private void writePom( String pom, File scm, String pomFile )
        throws Exception
    {
        FileWriter stream = new FileWriter( new File( scm, pomFile ) );

        PrintWriter output = new PrintWriter( stream );

        output.println( pom );

        output.close();

        stream.close();
    }
*/
}
