package org.codehaus.continuum.store.hibernate;

/*
 * LICENSE
 */

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.UserType;

import org.codehaus.continuum.project.ContinuumProjectState;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumProjectStateType.java,v 1.1 2004-07-27 00:06:07 trygvis Exp $
 */
public class ContinuumProjectStateType
    implements UserType
{
    private static Map intIndex;
    private static Map stateIndex;

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
