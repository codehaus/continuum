package org.codehaus.continuum.notification;

/*
 * The MIT License
 *
 * Copyright (c) 2004, Jason van Zyl and Trygve Laugst�l
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

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumNotificationDispatcher.java,v 1.1 2005-03-09 20:06:42 trygvis Exp $
 */
public interface ContinuumNotificationDispatcher
{
    String ROLE = ContinuumNotificationDispatcher.class.getName();

    String MESSAGE_ID_BUILD_STARTED = "BuildStarted";

    String MESSAGE_ID_CHECKOUT_STARTED = "CheckoutStarted";

    String MESSAGE_ID_CHECKOUT_COMPLETE = "CheckoutComplete";

    String MESSAGE_ID_RUNNING_GOALS = "RunningGoals";

    String MESSAGE_ID_GOALS_COMPLETED = "GoalsCompleted";

    String MESSAGE_ID_BUILD_COMPLETE = "BuildComplete";

    String CONTEXT_BUILD = "build";

    String CONTEXT_PROJECT = "project";

    void buildStarted( ContinuumBuild build );

    void checkoutStarted( ContinuumBuild build );

    void checkoutComplete( ContinuumBuild build );

    void runningGoals( ContinuumBuild build );

    void goalsCompleted( ContinuumBuild build );

    void buildComplete( ContinuumBuild build );
}
