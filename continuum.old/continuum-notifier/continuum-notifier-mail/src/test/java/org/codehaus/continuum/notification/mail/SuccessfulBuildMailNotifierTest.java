package org.codehaus.continuum.notification.mail;

/*
 * LICENSE
 */

import org.codehaus.continuum.notification.AbstractSuccessfulBuildNotifierTest;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: SuccessfulBuildMailNotifierTest.java,v 1.1 2004-07-27 00:06:09 trygvis Exp $
 */
public class SuccessfulBuildMailNotifierTest
    extends AbstractSuccessfulBuildNotifierTest
{
    protected String getProjectScmUrl()
    {
        return "scm:test:foo";
    }

    protected String getProjectType()
    {
        return "maven2";
    }
    
    protected String getNotifierRoleHint()
    {
        return "mail";
    }
}
