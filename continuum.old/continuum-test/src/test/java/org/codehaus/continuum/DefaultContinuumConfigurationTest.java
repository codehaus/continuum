package org.codehaus.continuum;

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

import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultContinuumConfigurationTest.java,v 1.8 2004-10-09 13:00:47 trygvis Exp $
 */
public class DefaultContinuumConfigurationTest
    extends PlexusTestCase
{
//    private final static String NEWLINE = System.getProperty( "line.separator" );

//    private String host = "127.0.0.1";
//    private int triggerPort = 10000;
//    private int registrarPort = 10001;

//    private String project1Url = "http://cvs.plexus.codehaus.org/viewcvs.cgi/*checkout*/plexus/plexus-components/native/continuum/src/test-projects/project1/project.xml?root=codehaus";
//    private String project2Url = "http://cvs.plexus.codehaus.org/viewcvs.cgi/*checkout*/plexus/plexus-components/native/continuum/src/test-projects/project2/project.xml?root=codehaus";

    /**
     * This test tests the default configuration of the continuum server.
     */
    public void testConfiguration()
        throws Exception
    {
/*
        Continuum continuum = (Continuum)lookup( Continuum.class.getName() ) ;

        // starts the smtpd
        SynapseServer synapse = (SynapseServer) lookup( SynapseServer.ROLE );

        Queue queue = (Queue)lookup( Queue.ROLE );

        System.err.println( "Testing continuum..." );

        register( project1Url );

        register( project2Url );

        trigger( "plexus:continuum-project1", 0 );

        // build project1
//        result = trigger( "plexus/continuum-project2" );
//        assertEquals( "OK" + NEWLINE, result );

        System.err.println( "Test complete" );

        // TODO: wait for notification of build

        int waitTime = 10000;
        int waitInterval = 1000;

        Collection messages = null;

        while ( waitTime > 0 )
        {
            messages = queue.getMessagesForDelivery();

            if ( messages.size() > 0 )
                break;

            Thread.sleep( waitInterval );
            waitTime -= waitInterval;
        }

        if ( waitTime <= 0 )
            fail( "Timeout while waiting for build message." );

        assertNotNull( messages );

        assertEquals( 1, messages.size() );

        // TODO: assert successful build

        release( queue );

        release( synapse );

        release( continuum );
*/
    }
/*
    public void testFoo()
        throws Exception
    {
        String result;

        result = trigger( "foo" );
        assertTrue( result.indexOf( NEWLINE ) >= 0 );
        assertEquals( "ERROR", result.substring( 0, result.indexOf( NEWLINE ) ) );
    }
*//*
    private void register( String url )
        throws Exception
    {
        String result = NetCat.write( host, registrarPort, url + NEWLINE );

        assertEquals( "OK" + NEWLINE, result );
    }

    private void trigger( String instruction, int buildNo )
        throws Exception
    {
        String result = NetCat.write( host, triggerPort, instruction + NEWLINE );

        String[] lines = StringUtils.split( result, NEWLINE );

        assertEquals( 3, lines.length );

        // The message
        assertEquals( "OK", lines[0] );

        // The build number
        assertEquals( Integer.toString( buildNo ), lines[1] );
    }
*/
}
