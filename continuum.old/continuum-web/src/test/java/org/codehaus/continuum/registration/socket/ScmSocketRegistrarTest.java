package org.codehaus.continuum.registration.socket;

/*
 * LICENSE
 */

import org.codehaus.continuum.AbstractContinuumTest;
import org.codehaus.continuum.Continuum;
import org.codehaus.continuum.network.ConnectionFactory;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.socket.SimpleSocket;
import org.codehaus.continuum.store.tx.StoreTransactionManager;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ScmSocketRegistrarTest.java,v 1.2 2004-10-31 15:54:50 trygvis Exp $
 */
public class ScmSocketRegistrarTest
    extends AbstractContinuumTest
{
    private final static int PORT = 54321;

    public void testRegistration()
        throws Exception
    {
        lookup( ConnectionFactory.ROLE, "scm-registrar-server" );

        SimpleSocket socket = new SimpleSocket( "localhost", PORT );

        String scmUrl = "scm:local:src/test/repository:scm-url";

        socket.writeLine( scmUrl );

        String line1 = socket.readLine();

        assertNotNull( line1 );

        assertEquals( "OK", line1 );

        String line2 = socket.readLine();

        assertNotNull( line2 );

        assertTrue( line2.startsWith( "id=" ) );

        String projectId = line2.substring( 3 );

        Continuum continuum = getContinuum();

        StoreTransactionManager txManager = getStoreTransactionManager();

        txManager.begin();

        ContinuumProject project = continuum.getProject( projectId );

        assertEquals( "Scm Socket Registrar Test Project", project.getName() );

        assertEquals( scmUrl, project.getScmUrl() );

        assertEquals( "maven2", project.getType() );

        txManager.commit();
    }
}
