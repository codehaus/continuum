package org.codehaus.continuum.builder.maven2;

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

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.project.MavenProject;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.project.AbstractProjectDescriptor;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: Maven2ProjectDescriptor.java,v 1.4 2004-07-27 05:42:12 trygvis Exp $
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
