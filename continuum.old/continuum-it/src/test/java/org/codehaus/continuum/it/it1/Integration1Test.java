package org.codehaus.continuum.it.it1;

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

import junit.framework.TestCase;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: Integration1Test.java,v 1.2 2004-10-15 13:01:03 trygvis Exp $
 */
public class Integration1Test
    extends TestCase
{
    public void testFoo()
    {
    }
/*
    private PlexusContainer container;

    private HibernateSessionService sessionService;

    private ContinuumStore store;

    private StoreTransactionManager txManager;

    private ContinuumWeb web;

    private Continuum continuum;

    public void setUp()
        throws Exception
    {
        container = TestUtils.setUpPlexus();

        TestUtils.setContainer( container );

        IntegrationTestUtils.setUpHibernate();

        sessionService = (HibernateSessionService) container.lookup( HibernateSessionService.ROLE );

        store = (ContinuumStore) container.lookup( ContinuumStore.ROLE );

        txManager = (StoreTransactionManager) container.lookup( StoreTransactionManager.ROLE );

        web = (ContinuumWeb) container.lookup( ContinuumWeb.ROLE );

        continuum = (Continuum) container.lookup( Continuum.ROLE );
    }

    public void tearDown()
        throws Exception
    {
        sessionService.closeSession();

        container.release( store );

        container.release( web );

        container.release( continuum );

        container.dispose();

        Connection connection = DriverManager.getConnection( "jdbc:hsqldb:.", "sa", "" );

        try
        {
            Statement statement = connection.createStatement();

            statement.execute( "SHUTDOWN IMMEDIATELY" );

            statement.close();
        }
        catch( Exception ex )
        {
            ex.printStackTrace( System.err );
        }

        connection.close();
    }

    public void testContinuum()
        throws Exception
    {
        String projectName = "Intergration Test Project 1";

        String projectScmUrl = "scm:test:src/test/repositories:normal";

        String projectNagEmailAddress = "foo@bar";

        String projectVersion = "1.0";

        // Add the project
        txManager.begin();

        String projectId = continuum.addProject( projectName, projectScmUrl, projectNagEmailAddress, projectVersion, "maven2" );

        assertNotNull( projectId );

        ContinuumProject project = store.getProject( projectId );

        assertEquals( ContinuumProjectState.NEW, project.getState() );

        assertNotNull( project.getDescriptor() );

        assertEquals( Maven2ProjectDescriptor.class, project.getDescriptor().getClass() );

        txManager.commit();

        // Build the project
        String buildId = continuum.buildProject( projectId );

        ContinuumBuild build = TestUtils.waitForBuild( buildId );

        ExternalMaven2BuildResult result = (ExternalMaven2BuildResult)build.getBuildResult();

        if ( !build.getState().equals( ContinuumProjectState.OK ) )
        {
            System.err.println( "************");
            System.out.println( result.getStandardOutput() ); System.out.flush();
            System.err.println( "************");

            System.err.println( "************");
            System.out.println( result.getStandardError() ); System.out.flush();
            System.err.println( "************");
        }

        System.err.println( "Build id: " + build.getId() );

        assertEquals( ContinuumProjectState.OK, build.getState() );
    }
*/
}
