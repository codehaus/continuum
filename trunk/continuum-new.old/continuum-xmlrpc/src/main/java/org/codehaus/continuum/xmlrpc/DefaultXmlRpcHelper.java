package org.codehaus.continuum.xmlrpc;

/*
 * Copyright 2004-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultXmlRpcHelper.java,v 1.1.1.1 2005-03-20 22:59:13 trygvis Exp $
 */
public class DefaultXmlRpcHelper
    extends AbstractLogEnabled
    implements XmlRpcHelper
{
    private Set EXCLUDED_METHODS = new HashSet( Arrays.asList( new String[] {
        "getClass"
    } ) );

    // ----------------------------------------------------------------------
    // XmlRpcHelper Implementation
    // ----------------------------------------------------------------------

    public Hashtable objectToHashtable( Object object )
        throws IllegalAccessException, InvocationTargetException
    {
        Hashtable hashtable = new Hashtable();

        Method[] methods = object.getClass().getMethods();

        for ( int i = 0; i < methods.length; i++ )
        {
            Method method = methods[ i ];

            String name = method.getName();

            // Only call getters
            if ( !name.startsWith( "get" ) || name.length() <= 3 )
            {
                continue;
            }

            // Only call methods without arguments
            if ( method.getParameterTypes().length != 0 )
            {
                continue;
            }

            // No use in calling methods that doesn't return anything
            if ( method.getReturnType().equals( Void.TYPE ) )
            {
                continue;
            }

            if ( EXCLUDED_METHODS.contains( name ) )
            {
                continue;
            }

            // ----------------------------------------------------------------------
            // Get the value
            // ----------------------------------------------------------------------

            Object value = method.invoke( object, new Object[ 0 ] );

            // ----------------------------------------------------------------------
            // Convert the value to a String
            // ----------------------------------------------------------------------

            if ( value == null )
            {
                continue;
            }
            else if ( value instanceof String )
            {
            }
            else if ( value instanceof Number )
            {
                value = value.toString();
            }
            else
            {
                value = objectToHashtable( value );
            }

            // ----------------------------------------------------------------------
            // Rewrite the name from the form 'getFoo' to 'foo'.
            // ----------------------------------------------------------------------

            name = name.substring( 3 );

            name = StringUtils.uncapitalise( name );

            // ----------------------------------------------------------------------
            //
            // ----------------------------------------------------------------------

            hashtable.put( name, value );
        }

        return hashtable;
    }
}
