package org.codehaus.continuum.store.hibernate;

/*
 * LICENSE
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
 * @version $Id: ExecutionResponseType.java,v 1.1 2004-07-27 00:06:07 trygvis Exp $
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
