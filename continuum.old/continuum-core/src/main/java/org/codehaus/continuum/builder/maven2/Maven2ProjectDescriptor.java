package org.codehaus.continuum.builder.maven2;

/*
 * LICENSE
 */

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.project.MavenProject;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.project.AbstractProjectDescriptor;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: Maven2ProjectDescriptor.java,v 1.3 2004-07-27 00:06:04 trygvis Exp $
 */
public class Maven2ProjectDescriptor
    extends AbstractProjectDescriptor
{
    /** */
    private List goals;

    private String pom;

    private transient MavenProject mavenProject;

    /**
     * @return Returns the goals.
     */
    public List getGoals()
    {
        if ( goals == null )
        {
            goals = new ArrayList();
        }

        return goals;
    }

    /**
     * @param goals The goals to set.
     */
    public void setGoals( List goals )
    {
        this.goals = goals;
    }

    /**
     * @return Returns the pom.
     */
    public String getPom()
    {
        return pom;
    }

    /**
     * @param pom The pom to set.
     */
    public void setPom( String pom )
    {
        this.pom = pom;
    }

    /**
     * @return Returns the mavenProject.
     * @throws ContinuumException Thrown if the mavenProject isn't set.
     */
    public MavenProject getMavenProject()
        throws ContinuumException
    {
        return mavenProject;
    }

    /**
     * @param mavenProject The mavenProject to set.
     */
    public void setMavenProject( MavenProject mavenProject )
    {
        this.mavenProject = mavenProject;
    }
}
