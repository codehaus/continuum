package org.codehaus.continuum.builder.shell;

/*
 * Copyright (c) 2004, Jason van Zyl and Trygve Laugst�l
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

import org.codehaus.continuum.project.AbstractProjectDescriptor;

import java.util.Map;
import java.util.HashMap;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ShellProjectDescriptor.java,v 1.1.1.1 2004-10-28 04:32:53 jvanzyl Exp $
 */
public class ShellProjectDescriptor
    extends AbstractProjectDescriptor
{
    // ----------------------------------------------------------------------
    // o we need to know where tests go so we can look at the output
    // o we need developers lists for the blame mechanism
    // o we need general parameters for the command line creation
    // ----------------------------------------------------------------------

    private String name;

    private String scmConnection;

    private String nagEmailAddress;

    private String version;

    private Map options = new HashMap();

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getScmConnection()
    {
        return scmConnection;
    }

    public void setScmConnection( String scmConnection )
    {
        this.scmConnection = scmConnection;
    }

    public String getNagEmailAddress()
    {
        return nagEmailAddress;
    }

    public void setNagEmailAddress( String nagEmailAddress )
    {
        this.nagEmailAddress = nagEmailAddress;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion( String version )
    {
        this.version = version;
    }

    public Map getOptions()
    {
        return options;
    }
}
