package org.codehaus.continuum.notification.mail;

/*
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

import org.codehaus.continuum.notification.AbstractSuccessfulBuildNotifierTest;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: SuccessfulBuildMailNotifierTest.java,v 1.4 2004-09-07 16:22:17 trygvis Exp $
 */
public class SuccessfulBuildMailNotifierTest
    extends AbstractSuccessfulBuildNotifierTest
{
    // ----------------------------------------------------------------------
    // Setup
    // ----------------------------------------------------------------------

    protected void setUpNotifier()
    {
        System.setProperty( "maven.repo.local", "/home/trygvis/.maven/repository" );
    }

    protected String getProjectScmUrl()
    {
        return "scm:test:src/test/repository:success";
    }

    protected String getProjectType()
    {
        return "maven2";
    }

    protected String getNotifierRoleHint()
    {
        return "mail";
    }

    // ----------------------------------------------------------------------
    // Assertions
    // ----------------------------------------------------------------------

    protected void postBuildComplete()
        throws Exception
    {
        MailContinuumNotifier notifier = (MailContinuumNotifier) getContinuumNotifier( "mail" );

        String lastMessage = notifier.getLastMessage();

        System.out.println("-------");
        System.out.println("Email sent");
        System.out.println("-------");
        System.out.print(lastMessage);
        System.out.println("-------");
    }
}
