package org.apache.maven.continuum.project;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: AntProject.java,v 1.1 2005-04-05 06:09:39 jvanzyl Exp $
 */
public class AntProject
    extends ShellProject
{
    private String targets;

    public String getTargets()
    {
        return targets;
    }

    public void setTargets( String targets )
    {
        this.targets = targets;
    }
}
