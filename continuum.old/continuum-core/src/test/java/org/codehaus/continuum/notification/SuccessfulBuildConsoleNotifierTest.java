package org.codehaus.continuum.notification;

/*
 * LICENSE
 */

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: SuccessfulBuildConsoleNotifierTest.java,v 1.1 2004-07-27 00:06:07 trygvis Exp $
 */
public class SuccessfulBuildConsoleNotifierTest
    extends AbstractSuccessfulBuildNotifierTest
{
    protected String getNotifierRoleHint()
    {
        return "console";
    }
}
