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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.UserType;

import org.codehaus.continuum.project.ContinuumProjectState;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumProjectStateType.java,v 1.3 2004-10-15 13:01:05 trygvis Exp $
 */
public class ContinuumProjectStateType
    implements UserType
{
    public boolean isMutable()
    {
        return false;
    }

    public Object deepCopy( Object value )
    {
        return (ContinuumProjectState) value;
    }

    public boolean equals( Object x, Object y )
    {
        return ( x == y );
    }

    public Class returnedClass()
    {
        return ContinuumProjectState.class;
    }

    public int[] sqlTypes()
    {
        return new int[]{ Types.INTEGER };
    }

    public Object nullSafeGet( ResultSet rs, String[] names, Object owner )
        throws HibernateException, SQLException
    {
        Integer valueObject = (Integer) Hibernate.INTEGER.nullSafeGet( rs, names[0] );

        if ( valueObject == null )
        {
            return null;
        }

        int value = valueObject.intValue();

        switch( value )
        {
        case 1:
            return ContinuumProjectState.NEW;
        case 2:
            return ContinuumProjectState.OK;
        case 3:
            return ContinuumProjectState.FAILED;
        case 4:
            return ContinuumProjectState.ERROR;
        case 5:
            return ContinuumProjectState.BUILD_SIGNALED;
        case 6:
            return ContinuumProjectState.BUILDING;
        }

        throw new HibernateException( "Unknown state: " + value );
    }

    public void nullSafeSet( PreparedStatement stmt, Object value, int index )
        throws HibernateException, SQLException
    {
        ContinuumProjectState state = (ContinuumProjectState) value;

        int intValue;

        if ( value == null )
        {
            Hibernate.INTEGER.nullSafeSet( stmt, null, index );

            return;
        }

        if ( state == ContinuumProjectState.NEW )
        {
            intValue = 1;
        }
        else if ( state == ContinuumProjectState.OK )
        {
            intValue = 2;
        }
        else if ( state == ContinuumProjectState.FAILED )
        {
            intValue = 3;
        }
        else if ( state == ContinuumProjectState.ERROR )
        {
            intValue = 4;
        }
        else if ( state == ContinuumProjectState.BUILD_SIGNALED )
        {
            intValue = 5;
        }
        else if ( state == ContinuumProjectState.BUILDING )
        {
            intValue = 6;
        }
        else
        {
            throw new HibernateException( "Unknown state: " + state );
        }

        Hibernate.INTEGER.nullSafeSet( stmt, new Integer( intValue ), index );
    }

    /*
    public int toInt()
    {
        initializeKeys();

        Integer value = (Integer)intIndex.get( this );

        if ( value == null )
        {
            throw new NoSuchElementException( "No such element: " + this );
        }

        System.out.println( this + "=>" + value.intValue() );

        return value.intValue();
    }

    public static ContinuumProjectStateUserType fromInt( int value )
    {
        initializeKeys();

        ContinuumProjectStateUserType state = (ContinuumProjectStateUserType)stateIndex.get( new Integer( value ) );

        if ( state == null )
        {
            throw new NoSuchElementException( "No such element: " + value );
        }

        System.out.println( value + "=>" + state );

        return state;
    }

    private static synchronized void initializeKeys()
    {
        if ( intIndex == null )
        {
            intIndex = new HashMap();

            intIndex.put( new Integer( 1 ), NEW );
            intIndex.put( new Integer( 2 ), OK );
            intIndex.put( new Integer( 3 ), FAILED );
            intIndex.put( new Integer( 4 ), ERROR );
            intIndex.put( new Integer( 5 ), BUILD_SIGNALED );
            intIndex.put( new Integer( 6 ), BUILDING );

            stateIndex = new HashMap();

            stateIndex.put( NEW, new Integer( 1 ) );
            stateIndex.put( OK, new Integer( 2 ) );
            stateIndex.put( FAILED, new Integer( 3 ) );
            stateIndex.put( ERROR, new Integer( 4 ) );
            stateIndex.put( BUILD_SIGNALED, new Integer( 5 ) );
            stateIndex.put( BUILDING, new Integer( 6 ) );
        }
    }
*/
}
