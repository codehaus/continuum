package org.codehaus.continuum.store.hibernate;

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

import com.thoughtworks.xstream.XStream;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.UserType;

import org.apache.maven.ExecutionResponse;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ExecutionResponseType.java,v 1.2 2004-07-27 05:42:15 trygvis Exp $
 */
public class ExecutionResponseType
    implements UserType
{
    private static XStream xStream = new XStream();

    public boolean isMutable()
    {
        return false;
    }

    public Object deepCopy( Object value )
    {
        return (ExecutionResponse) value;
    }

    public boolean equals( Object x, Object y )
    {
        return ( x == y );
    }

    public Class returnedClass()
    {
//        return ExecutionResponse.class;
        return String.class;
    }

    public int[] sqlTypes()
    {
        return new int[]{ Types.CLOB };
    }

    public Object nullSafeGet( ResultSet rs, String[] names, Object owner )
        throws HibernateException, SQLException
    {
        String valueObject = (String) Hibernate.STRING.nullSafeGet( rs, names[0] );

        if ( valueObject == null )
        {
            return null;
        }

        String value = valueObject.toString();

        return value;
        //TODO: Xstream;
//        ExecutionResponse executionResponse = (ExecutionResponse)xStream.fromXML( value );

//        return executionResponse;
    }

    public void nullSafeSet( PreparedStatement stmt, Object value, int index )
        throws HibernateException, SQLException
    {
        ExecutionResponse executionResponse = (ExecutionResponse) value;

        if ( executionResponse == null )
        {
            Hibernate.STRING.nullSafeSet( stmt, null, index );

            return;
        }

        String stringValue = xStream.toXML( executionResponse );

        Hibernate.CLOB.nullSafeSet( stmt, Hibernate.createClob( stringValue ), index );
    }
}
