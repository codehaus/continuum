package org.codehaus.continuum.notification;

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

import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.store.tx.StoreTransactionManager;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AbstractSuccessfulBuildNotifierTest.java,v 1.4 2004-10-06 13:33:50 trygvis Exp $
 */
public abstract class AbstractSuccessfulBuildNotifierTest
    extends AbstractNotifierTest
{
    protected abstract String getNotifierRoleHint();

    public void testSuccessfulBuild()
        throws Exception
    {
        StoreTransactionManager txManager = getStoreTransactionManager();

        ContinuumNotifier notifier = getContinuumNotifier( getNotifierRoleHint() );

        // --- -- -

        preBuildStarted();

        ContinuumBuild buildResult = build();

        txManager.begin();

        notifier.buildStarted( buildResult );

        txManager.commit();

        postBuildStarted();

        // --- -- -

        txManager.begin();

        notifier.checkoutStarted( buildResult );

        txManager.commit();

        postCheckoutStarted();

        // --- -- -

        txManager.begin();

        notifier.checkoutComplete( buildResult );

        txManager.commit();

        postCheckoutComplete();

        // --- -- -

        txManager.begin();

        notifier.runningGoals( buildResult );

        txManager.commit();

        postRunningGoals();

        // --- -- -

        txManager.begin();

        notifier.goalsCompleted( buildResult );

        txManager.commit();

        postGoalsCompleted();

        // --- -- -

        txManager.begin();

        notifier.buildComplete( buildResult );

        txManager.commit();

        postBuildComplete();
    }
}
