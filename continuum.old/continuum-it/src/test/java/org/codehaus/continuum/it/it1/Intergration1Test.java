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

import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;

import junit.framework.TestCase;
import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.tool.hbm2ddl.SchemaExport;

import org.codehaus.continuum.Continuum;
import org.codehaus.continuum.builder.maven2.Maven2ProjectDescriptor;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ContinuumProjectState;
import org.codehaus.continuum.store.ContinuumStore;
import org.codehaus.continuum.web.ContinuumWeb;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.hibernate.DefaultHibernateService;
import org.codehaus.plexus.hibernate.HibernateService;
import org.codehaus.plexus.hibernate.HibernateSessionService;
import org.codehaus.plexus.util.FileUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: Intergration1Test.java,v 1.1 2004-07-29 16:11:45 trygvis Exp $
 */
public class Intergration1Test
    extends TestCase
{
    private DefaultPlexusContainer container;

    private HibernateSessionService sessionService;

    private ContinuumStore store;

    private ContinuumWeb web;

    private Continuum continuum;

    private static String buildId;

    private interface StateChecker
    {
        boolean checkState()
            throws Exception;
    }

    public void setUp()
        throws Exception
    {
        container = new DefaultPlexusContainer();

        container.getContext().put( "basedir", new File( "" ).getAbsolutePath() );

        File plexusHome = getTestFile( "target/plexus-home" );

        if ( plexusHome.exists() )
        {
            FileUtils.deleteDirectory( plexusHome );
        }

        container.getContext().put( "plexus.home", plexusHome.getAbsolutePath() );

        FileUtils.mkdir( new File( plexusHome, "apps/maven2/plugins" ).getAbsolutePath() );

        FileUtils.mkdir( new File( plexusHome, "apps/continuumweb/lib" ).getAbsolutePath() );

        container.setConfigurationResource( new InputStreamReader( getCustomConfiguration().openStream() ) );

        container.initialize();

        container.start();

        setupHibernate();

        sessionService = (HibernateSessionService) container.lookup( HibernateSessionService.ROLE );

        store = (ContinuumStore) container.lookup( ContinuumStore.ROLE );

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
    }

    protected String getTestPath( String path )
        throws Exception
    {
        String basedir = container.getContext().get( "basedir" ).toString();

        return new File( basedir, path ).getAbsolutePath();
    }

    protected File getTestFile( String path )
        throws Exception
    {
        String basedir = container.getContext().get( "basedir" ).toString();

        return new File( basedir, path );
    }

    private URL getCustomConfiguration()
        throws Exception
    {
        URL config = Thread.currentThread().getContextClassLoader().getResource( "conf/plexus.conf" );

        assertNotNull( "Missing configuration", config );

        return config;
    }

    private void setupHibernate()
        throws Exception
    {
        container.lookup( HibernateSessionService.ROLE );

        DefaultHibernateService hibernate = (DefaultHibernateService) container.lookup( HibernateService.ROLE );

        Configuration configuration = hibernate.getConfiguration();

        SchemaExport exporter = new SchemaExport( configuration );

        exporter.setDelimiter( ";" );

        exporter.create( false, true );
    }

    public void testContinuum()
        throws Exception
    {
        String projectName = "Intergration Test Project 1";

        String projectScmUrl = "scm:test:../repositories/:normal";

        // Add the project
        String projectId = continuum.addProject( projectName, projectScmUrl, "maven2" );

        assertNotNull( projectId );

        ContinuumProject project = store.getProject( projectId );

        assertEquals( ContinuumProjectState.NEW, project.getState() );

        assertNotNull( project.getDescriptor() );

        assertEquals( Maven2ProjectDescriptor.class, project.getDescriptor().getClass() );

        // Build the project
        buildId = continuum.buildProject( projectId );
/*
        project = store.getProject( projectId );

        assertEquals( ContinuumProjectState.BUILD_SIGNALED, project.getState() );

        ContinuumBuild build = store.getBuild( buildId );

        assertEquals( ContinuumProjectState.BUILD_SIGNALED, build.getState() );

        waitForState( 10000, new StateChecker() {
            public boolean checkState()
                throws Exception
            {
                sessionService.closeSession();
                sessionService.getSession();

                ContinuumBuild build2 = store.getBuild( Intergration1Test.buildId );

                return build2.getState() == ContinuumProjectState.BUILDING ||
                       build2.getState() == ContinuumProjectState.OK;
            }
        } );
*/
        waitForState( 10000, new StateChecker() {
            public boolean checkState()
                throws Exception
            {
                sessionService.closeSession();
                sessionService.getSession();

                ContinuumBuild build2 = store.getBuild( Intergration1Test.buildId );

                return build2.getState() == ContinuumProjectState.OK;
            }
        } );
    }

    public void waitForState( int maxWaitPeriod, StateChecker checker )
        throws Exception
    {
        int interval = 1000;

        int rest = maxWaitPeriod;

        while( true )
        {
            if ( checker.checkState() )
            {
                break;
            }

            Thread.sleep( interval );

            rest -= interval;

            if ( rest <= 0 )
            {
                fail( "Timeout" );
            }
        }
    }
}
