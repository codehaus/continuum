package org.codehaus.continuum.it.storebuilder;

/*
 * LICENSE
 */

import org.codehaus.continuum.builder.shell.MavenContinuumBuilder;
import org.codehaus.continuum.builder.shell.ShellBuildResult;
import org.codehaus.continuum.builder.shell.ShellProjectDescriptor;
import org.codehaus.continuum.it.it1.IntegrationTestUtils;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ContinuumProjectState;
import org.codehaus.continuum.store.hibernate.HibernateContinuumStore;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: Maven1HibernateTest.java,v 1.2 2004-10-31 17:07:41 trygvis Exp $
 */
public class Maven1HibernateTest
	extends AbstractStoreBuilderTest
{
    public Maven1HibernateTest()
    {
        super( MavenContinuumBuilder.class, "maven", HibernateContinuumStore.class, "hibernate" );
    }

    // ----------------------------------------------------------------------
    // 
    // ----------------------------------------------------------------------

    public void setUp() throws Exception
    {
        super.setUp();

        IntegrationTestUtils.setUpHibernate();
    }

    protected void assertProject( ContinuumProject project )
		throws Exception
    {
        assertEquals( project.getDescriptor().getClass(), ShellProjectDescriptor.class );

        assertEquals( "The project name isn't correct", "Maven 1 And Hibernate Test Project", project.getName() );
    }

    protected void assertBuild( ContinuumBuild build )
    	throws Exception
	{
        assertNotNull( "THe build is null", build );

        assertNotNull( "The build result is null", build.getBuildResult() );

        assertEquals( "The build result isn't of the expected type.", build.getBuildResult().getClass(), ShellBuildResult.class );

        if ( !build.getState().equals( ContinuumProjectState.OK ) )
        {
            ShellBuildResult result = (ShellBuildResult) build.getBuildResult();

            System.err.println("stdout:");
            System.err.println(result.getStandardOutput());
            System.err.println();
            System.err.println("stderr:");
            System.err.println(result.getStandardError());
            System.err.println();
        }
	}
}
