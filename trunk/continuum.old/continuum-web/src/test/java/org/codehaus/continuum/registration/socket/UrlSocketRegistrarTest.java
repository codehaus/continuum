package org.codehaus.continuum.registration.socket;

/*
 * LICENSE
 */

import java.net.URL;

import org.codehaus.continuum.AbstractContinuumTest;
import org.codehaus.continuum.Continuum;
import org.codehaus.continuum.network.ConnectionFactory;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.socket.SimpleSocket;
import org.codehaus.continuum.store.tx.StoreTransactionManager;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: UrlSocketRegistrarTest.java,v 1.2 2004-10-31 15:54:50 trygvis Exp $
 */
public class UrlSocketRegistrarTest
    extends AbstractContinuumTest
{
    private final static int PORT = 54321;

    public void testRegistration()
        throws Exception
    {
        lookup( ConnectionFactory.ROLE, "url-registrar-server" );

        SimpleSocket socket = new SimpleSocket( "localhost", PORT );

        URL url = getTestFile( "src/test/repository/pom-url/pom.xml" ).toURL();

        socket.writeLine( url.toExternalForm() );

        String line1 = socket.readLine();

        assertNotNull( line1 );

        if ( !line1.equals( "OK" ) )
        {
            String line = socket.readLine();

            while( line != null )
            {
                System.err.println( line );

                line = socket.readLine();
            }

            fail( "Error while adding project" );
        }

        String line2 = socket.readLine();

        assertNotNull( line2 );

        assertTrue( line2.startsWith( "id=" ) );

        String projectId = line2.substring( 3 );

        Continuum continuum = getContinuum();

        StoreTransactionManager txManager = getStoreTransactionManager();

        txManager.begin();

        ContinuumProject project = continuum.getProject( projectId );

        assertEquals( "Url Socket Registrar Test Project", project.getName() );

        assertEquals( "scm:local:src/test/repository:pom-url", project.getScmUrl() );

        assertEquals( "foo@bar", project.getNagEmailAddress() );

        assertEquals( "1.0.0", project.getVersion() );

        assertEquals( "maven2", project.getType() );

        txManager.commit();
    }
}
