package org.codehaus.continuum;

/*
 * LICENSE
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: TestUtils.java,v 1.1 2004-07-19 16:54:47 trygvis Exp $
 */
public abstract class TestUtils
{
    private TestUtils()
    {
    }

    public static List iteratorToList( Iterator it )
    {
        Assert.assertNotNull( it );

        List list = new ArrayList( 10 );

        while ( it.hasNext() )
        {
            list.add( it.next() );
        }

        return list;
    }
}
