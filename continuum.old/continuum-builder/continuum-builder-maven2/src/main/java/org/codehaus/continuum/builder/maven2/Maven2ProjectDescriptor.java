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

import org.codehaus.continuum.project.AbstractProjectDescriptor;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: Maven2ProjectDescriptor.java,v 1.6 2004-10-28 21:23:30 trygvis Exp $
 */
public class Maven2ProjectDescriptor
    extends AbstractProjectDescriptor
{
    /** */
    private List goals;

//    private String pom;

    // some selected fields from the POM
//    private String name;

//    private String scmUrl;

//    private String nagEmailAddress;

//    private String version;

//    private transient MavenProject mavenProject;

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
//    public String getPom()
//    {
//        return pom;
//    }

    /**
     * @param pom The pom to set.
     */
//    public void setPom( String pom )
//    {
//        this.pom = pom;
//    }

    /**
     * @return Returns the name.
     */
//    public String getName()
//    {
//        return name;
//    }

    /**
     * @param name The name to set.
     */
//    public void setName( String name )
//    {
//        this.name = name;
//    }

    /**
     * @return Returns the scmUrl.
     */
//    public String getScmUrl()
//    {
//        return scmUrl;
//    }

    /**
     * @param scmUrl The scmUrl to set.
     */
//    public void setScmUrl( String scmUrl )
//    {
//        this.scmUrl = scmUrl;
//    }

    /**
     * @return Returns the nag email address.
     */
//    public String getNagEmailAddress()
//    {
//        return nagEmailAddress;
//    }

    /**
     * @param nagEmailAddress The nag email address to set.
     */
//    public void setNagEmailAddress( String nagEmailAddress )
//    {
//        this.nagEmailAddress = nagEmailAddress;
//    }

    /**
     * @return Returns the version.
     */
//    public String getVersion()
//    {
//        return version;
//    }

    /**
     * @param version The version to set.
     */
//    public void setVersion( String version )
//    {
//        this.version = version;
//    }

    /**
     * @return Returns the mavenProject.
     */
//    public MavenProject getMavenProject()
//    {
//        return mavenProject;
//    }

    /**
     * @param mavenProject The mavenProject to set.
     */
//    public void setMavenProject( MavenProject mavenProject )
//    {
//        this.mavenProject = mavenProject;
//    }
}
