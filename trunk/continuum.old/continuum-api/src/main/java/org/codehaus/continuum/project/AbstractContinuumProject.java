package org.codehaus.continuum.project;

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

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AbstractContinuumProject.java,v 1.5 2004-10-24 20:39:04 trygvis Exp $
 */
public abstract class AbstractContinuumProject
    implements ContinuumProject
{
    /** */
    private String id;

    /** */
    private String type;

    /** */
    private String name;

    /** */
    private String scmUrl;

    /** */
    private String nagEmailAddress;

    /** */
    private String version;

    /** */
    private ContinuumProjectState state;

    /** */
    private ProjectDescriptor descriptor;

    /**
     * @return Returns the id.
     */
    public String getId()
    {
        return id;
    }

    /**
     * @param id The id to set.
     */
    public void setId( String id )
    {
        this.id = id;
    }

    /**
     * @return Returns the type.
     */
    public String getType()
    {
        return type;
    }

    /**
     * @param type The type to set.
     */
    public void setType( String type )
    {
        this.type = type;
    }

    /**
     * @return Returns the name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName( String name )
    {
        this.name = name;
    }

    /**
     * @return Returns the scm url.
     */
    public String getScmUrl()
    {
        return scmUrl;
    }

    /**
     * @param scmUrl The scm url to set.
     */
    public void setScmUrl( String scmUrl )
    {
        this.scmUrl = scmUrl;
    }

    /**
     * @return Returns the nag email address.
     */
    public String getNagEmailAddress()
    {
        return nagEmailAddress;
    }

    /**
     * @param nagEmailAddress The nag email address to set.
     */
    public void setNagEmailAddress( String nagEmailAddress )
    {
        this.nagEmailAddress = nagEmailAddress;
    }

    /**
     * @return Returns the version.
     */
    public String getVersion()
    {
        return version;
    }

    /**
     * @param version The version to set.
     */
    public void setVersion( String version )
    {
        this.version = version;
    }

    /**
     * @return Returns the state.
     */
    public ContinuumProjectState getState()
    {
        return state;
    }

    /**
     * @param state The state to set.
     */
    public void setState( ContinuumProjectState state )
    {
        this.state = state;
    }

    public ProjectDescriptor getDescriptor()
    {
        return descriptor;
    }

    public void setDescriptor( ProjectDescriptor descriptor )
    {
        this.descriptor = descriptor;
    }
/*
    public List getBuilds()
    {
        return builds;
    }

    protected void setBuilds( List builds )
    {
        this.builds = builds;
    }
*/
}
