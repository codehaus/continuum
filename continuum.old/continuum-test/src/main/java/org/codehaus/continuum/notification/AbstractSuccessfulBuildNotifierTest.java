package org.codehaus.continuum.notification;

/*
 * LICENSE
 */

import org.codehaus.continuum.project.ContinuumBuild;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AbstractSuccessfulBuildNotifierTest.java,v 1.1 2004-07-27 00:06:11 trygvis Exp $
 */
public abstract class AbstractSuccessfulBuildNotifierTest
    extends AbstractNotifierTest
{
    protected abstract String getNotifierRoleHint();

    public void testSuccessfulBuild()
        throws Exception
    {
        ContinuumNotifier notifier = getContinuumNotifier( getNotifierRoleHint() );

        ContinuumBuild buildResult = getBuildResult();

        notifier.buildStarted( buildResult );

        postBuildStarted();

        notifier.checkoutStarted( buildResult );

        postCheckoutStarted();

        notifier.checkoutComplete( buildResult );

        postCheckoutComplete();

        notifier.runningGoals( buildResult );

        postRunningGoals();

        notifier.goalsCompleted( buildResult );

        postGoalsCompleted();

        notifier.buildComplete( buildResult );

        postBuildComplete();
    }
}
