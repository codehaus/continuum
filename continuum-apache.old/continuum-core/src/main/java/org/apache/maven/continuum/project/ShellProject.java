/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.apache.maven.continuum.project;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: ShellProject.java,v 1.1 2005-04-05 06:09:39 jvanzyl Exp $
 */
public class ShellProject
    extends ContinuumProject
{
    private String executable;

    public String getExecutable()
    {
        return executable;
    }

    public void setExecutable( String executable )
    {
        this.executable = executable;
    }
}
