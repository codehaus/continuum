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

import org.codehaus.continuum.AbstractContinuumTest;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.store.ContinuumStore;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AbstractNotifierTest.java,v 1.2 2004-07-29 04:27:41 trygvis Exp $
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
