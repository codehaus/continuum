package org.codehaus.continuum.notification;

/*
 * LICENSE
 */

import org.codehaus.continuum.AbstractContinuumTest;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.store.ContinuumStore;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AbstractNotifierTest.java,v 1.1 2004-07-27 00:06:11 trygvis Exp $
 */
public class AbstractNotifierTest
    extends AbstractContinuumTest
{
    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    protected void setUpNotifier()
        throws Exception
    {
    }

    protected void tearDownNotifier()
        throws Exception
    {
    }

    protected String getProjectScmUrl()
    {
        return "scm:test:foo";
    }

    protected String getProjectType()
    {
        return "test";
    }

    // ----------------------------------------------------------------------
    // Override these to assert the notifier.
    // ----------------------------------------------------------------------

    protected void postBuildStarted()
        throws Exception
    {
    }

    protected void postCheckoutStarted()
        throws Exception
    {
    }

    protected void postCheckoutComplete()
        throws Exception
    {
    }

    protected void postRunningGoals()
        throws Exception
    {
    }

    protected void postGoalsCompleted()
        throws Exception
    {
    }

    protected void postBuildComplete()
        throws Exception
    {
    }

    // ----------------------------------------------------------------------
    // Implementation of the test
    // ----------------------------------------------------------------------

    public final void setUp()
        throws Exception
    {
        super.setUp();

        setUpNotifier();
    }

    public final void tearDown()
        throws Exception
    {
        super.tearDown();
    }

    protected ContinuumBuild getBuildResult()
        throws Exception
    {
        ContinuumStore store = getContinuumStore( "memory" );

        String projectName = "Notifier Test Project";

        String projectId = store.addProject( projectName, getProjectScmUrl(), getProjectType() );

        String buildId = store.createBuild( projectId );

        ContinuumBuild buildResult = store.getBuild( buildId );

        return buildResult;
    }
}
