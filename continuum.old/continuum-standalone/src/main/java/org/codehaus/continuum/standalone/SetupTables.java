package org.codehaus.continuum.standalone;

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

import java.io.File;
import java.net.URL;

import org.codehaus.continuum.store.hibernate.HibernateUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: SetupTables.java,v 1.3 2004-08-26 10:13:49 trygvis Exp $
 */
public class SetupTables
{
    public static void main( String[] args )
        throws Exception
    {
        URL configuration = null;

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        if ( args.length > 0 )
        {
            String fileName = args[0];

            File file = new File( fileName );

            if ( !file.exists() )
            {
                System.err.println( "No such file: " + file );

                return;
            }

            configuration = file.toURL();
        }

        if ( configuration == null )
        {
            configuration = classLoader.getResource( "/hibernate.cfg.xml" );

            if ( configuration == null )
            {
                System.err.println( "Could not find hibernate configuration." );

                return;
            }
        }

        HibernateUtils.createDatabase( configuration );
    }
}
