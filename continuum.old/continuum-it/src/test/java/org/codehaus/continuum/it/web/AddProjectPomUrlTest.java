package org.codehaus.continuum.it.web;

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

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import org.codehaus.continuum.TestUtils;
import org.codehaus.continuum.store.ContinuumStore;
import org.codehaus.continuum.store.tx.StoreTransactionManager;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.formica.FormManager;
import org.codehaus.plexus.util.IOUtil;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AddProjectPomUrlTest.java,v 1.1 2004-10-06 14:27:04 trygvis Exp $
 */
public class AddProjectPomUrlTest
    extends AbstractContinuumWebTest
{
    private PlexusContainer container;

    public void setUp()
        throws Exception
    {
        super.setUp();

        container = TestUtils.setUpPlexus();

        setUpJetty( container );

        container.lookup( FormManager.ROLE );
    }

    public void tearDown()
        throws Exception
    {
        tearDownJetty();

        super.tearDown();
    }
/*
    public void testExecute()
        throws Exception
    {
        StoreTransactionManager txManager = getStoreTransactionManager();

        AddProjectPomUrl action = (AddProjectPomUrl) lookup( Action.ROLE, "addProjectPomUrl" );

        Map params = new HashMap();

        params.put( "addProject.pomUrl", "file://" + getTestFile( "src/test/resources/actions/addProject/pom.xml" ) );

        getContinuum();

        txManager.begin();

        action.execute( params );

        txManager.commit();
    }
*/
    public void testAddProjectPomUrl()
        throws Exception
    {
        URL url = new URL( "http://localhost:9999/continuum/continuum/?skipLogin=true&action=addProjectPomUrl&addProject.pomUrl=file://" + getTestFile( "src/test/resources/actions/addProject/pom.xml" ) );

        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        InputStream input = con.getInputStream();

        String page = new String( IOUtil.toByteArray( input ) );

        if ( false )
        {
            System.err.println( page );
        }

        IOUtil.close( input );

        ContinuumStore store = (ContinuumStore) container.lookup( ContinuumStore.ROLE );

        StoreTransactionManager txManager = (StoreTransactionManager) container.lookup( StoreTransactionManager.ROLE );

        txManager.begin();

        Iterator it = store.getAllProjects();

        assertTrue( it.hasNext() );

        it.next();

        assertFalse( it.hasNext() );

        txManager.commit();
    }
}
