package org.codehaus.continuum.web.model;

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
 * @version $Id: ProjectModel.java,v 1.4 2004-10-24 20:39:11 trygvis Exp $
 */
public class ProjectModel
{
    private String id;

    private String name;

    private String state;

    private String scmUrl;

    private String nagEmailAddress;

    private String version;

    private String type;

    private ProjectDescriptorModel descriptor;

    /**
     * @param name
     * @param scmUrl
     * @param nagEmailAddress
     * @param version
     * @param state
     * @param type
     * @param descriptor
     */
    public ProjectModel( String id, String name, String scmUrl, String nagEmailAddress, String version, String state, String type, ProjectDescriptorModel descriptor )
    {
        this.id = id;
        this.name = name;
        this.scmUrl = scmUrl;
        this.nagEmailAddress = nagEmailAddress;
        this.version = version;
        this.state = state;
        this.type = type;
        this.descriptor = descriptor;
    }

    /**
     * @return Returns the id.
     */
    public String getId()
    {
        return id;
    }

    /**
     * @return Returns the name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return Returns the scmUrl.
     */
    public String getScmUrl()
    {
        return scmUrl;
    }

    /**
     * @return Returns the nag email address.
     */
    public String getNagEmailAddress()
    {
        return nagEmailAddress;
    }

    /**
     * @return Returns the version
     */
    public String getVersion()
    {
        return version;
    }

    /**
     * @return Returns the state.
     */
    public String getState()
    {
        return state;
    }

    /**
     * @return Returns the type.
     */
    public String getType()
    {
        return type;
    }

    /**
     * @return Returns the descriptor.
     */
    public ProjectDescriptorModel getDescriptor()
    {
        return descriptor;
    }
}
