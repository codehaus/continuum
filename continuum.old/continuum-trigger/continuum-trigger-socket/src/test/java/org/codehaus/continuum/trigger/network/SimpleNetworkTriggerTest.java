package org.codehaus.continuum.trigger.network;

/*
 * LICENSE
 */

import org.codehaus.continuum.AbstractContinuumTest;
import org.codehaus.continuum.Continuum;
import org.codehaus.continuum.network.ConnectionFactory;
import org.codehaus.continuum.socket.SimpleSocket;
import org.codehaus.continuum.store.tx.StoreTransactionManager;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: SimpleNetworkTriggerTest.java,v 1.4 2004-10-28 17:52:45 trygvis Exp $
 */
public class SimpleNetworkTriggerTest
    extends AbstractContinuumTest
{
    private final static int DEFAULT_PORT = 40030;

    public void testNetworkTrigger()
        throws Exception
    {
        Continuum continuum = getContinuum();

        StoreTransactionManager txManager = getStoreTransactionManager();

        lookup( ConnectionFactory.ROLE, "simple-socket-trigger-server" );

        txManager.begin();

        String projectId = continuum.addProjectFromScm( "scm:local:src/test/repository:successful", "test");

        txManager.commit();

        SimpleSocket socket = new SimpleSocket( "localhost", DEFAULT_PORT );

        socket.writeLine( projectId );

        String line1 = socket.readLine();

        assertNotNull( line1 );

        assertEquals( "OK", line1 );

        String line2 = socket.readLine();

        assertNotNull( line2 );

        assertTrue( line2.startsWith( "id=" ) );

        String line3 = socket.readLine();

        assertNotNull( line3 );

        assertTrue( line3.startsWith( "Build of " ) );

        assertTrue( line3.endsWith( " scheduled." ) );

        socket.close();
    }
}
