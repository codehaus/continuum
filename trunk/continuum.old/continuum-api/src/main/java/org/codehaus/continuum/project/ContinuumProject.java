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

import java.io.Serializable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumProject.java,v 1.5 2004-10-24 20:39:04 trygvis Exp $
 */
public interface ContinuumProject
    extends Serializable
{
    /**
     * @return Returns the id.
     */
    String getId();

    /**
     * @param id The id to set.
     */
    void setId( String id );

    /**
     * @return Returns the type.
     */
    String getType();

    /**
     * @param type The type to set.
     */
    void setType( String type );

    /**
     * @return Returns the name.
     */
    String getName();

    /**
     * @param name The name to set.
     */
    void setName( String name );

    /**
     * @return Returns the scm url.
     */
    String getScmUrl();

    /**
     * @param scmUrl The scmUrl to set.
     */
    void setScmUrl( String scmUrl );

    String getNagEmailAddress();

    void setNagEmailAddress( String nagEmailAddress );

    String getVersion();

    void setVersion( String version );

    /**
     * @return Returns the state.
     */
    ContinuumProjectState getState();

    /**
     * @param state The state to set.
     */
    void setState( ContinuumProjectState state );

    ProjectDescriptor getDescriptor();

    void setDescriptor( ProjectDescriptor descriptor );
}
