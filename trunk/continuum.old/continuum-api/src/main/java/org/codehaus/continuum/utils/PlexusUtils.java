package org.codehaus.continuum.utils;

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

import org.codehaus.plexus.configuration.PlexusConfigurationException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: PlexusUtils.java,v 1.2 2004-07-27 05:42:11 trygvis Exp $
 */
public class PlexusUtils
{
    private PlexusUtils()
    {
    }

    public static void assertConfiguration( Object configuration, String name )
        throws PlexusConfigurationException
    {
        if( configuration == null )
        {
            throw new PlexusConfigurationException( "Missing configuration element: '" + name + "'." );
        }

        if ( configuration instanceof String )
        {
            String str = (String)configuration;

            if ( str.trim().length() == 0 )
            {
                throw new PlexusConfigurationException( "Misconfigured element '" + name + "': Element cannot be empty." );
            }
        }
    }

    public static void assertRequirement( Object requirement, String role )
        throws PlexusConfigurationException
    {
        if ( requirement == null )
        {
            throw new PlexusConfigurationException( "Missing requirement '" + role + "'." );
        }
    }
}
