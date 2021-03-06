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

import java.util.Hashtable;

import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: XmlRpcHelperTest.java,v 1.3 2005-03-23 16:14:53 trygvis Exp $
 */
public class XmlRpcHelperTest
    extends PlexusTestCase
{
    private static class SimleBean
    {
        private String foo;

        private int bar;

        private boolean bool;

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        public String getFoo()
        {
            return foo;
        }

        public int getBar()
        {
            return bar;
        }

        public boolean isBool()
        {
            return bool;
        }

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        public int get()
        {
            return 10;
        }
    }

    private static class ComplexBean
    {
        private SimleBean s1;

        private SimleBean s2;

        private String name;

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        public SimleBean getS1()
        {
            return s1;
        }

        public SimleBean getS2()
        {
            return s2;
        }

        public String getName()
        {
            return name;
        }

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        public void get()
        {
        }

        public void getFoo()
        {
        }

        public void getBar( int value )
        {
        }
    }

    public void testSimpleBean()
        throws Exception
    {
        XmlRpcHelper xmlRpcHelper = (XmlRpcHelper) lookup( XmlRpcHelper.ROLE );

        SimleBean bean = new SimleBean();

        bean.foo = "trygve";

        bean.bar = 24;

        bean.bool = true;

        Hashtable hashtable = xmlRpcHelper.objectToHashtable( bean );

        assertEquals( 3, hashtable.size() );

        assertProperty( "foo", "trygve", hashtable );

        assertProperty( "bar", "24", hashtable );

        assertProperty( "bool", "true", hashtable );
    }

    public void testComplexBean()
        throws Exception
    {
        XmlRpcHelper xmlRpcHelper = (XmlRpcHelper) lookup( XmlRpcHelper.ROLE );

        SimleBean s1 = new SimleBean();

        s1.foo = "trygve";

        s1.bar = 24;

        s1.bool = true;

        SimleBean s2 = new SimleBean();

        s2.foo = "anne";

        s2.bar = 22;

        s2.bool = false;

        ComplexBean complexBean = new ComplexBean();

        complexBean.s1 = s1;

        complexBean.s2 = s2;

        complexBean.name = "complex";

        Hashtable hashtable = xmlRpcHelper.objectToHashtable( complexBean );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        assertEquals( 3, hashtable.size() );

        assertProperty( "name", "complex", hashtable );

        assertTrue( hashtable.containsKey( "s1" ) );

        Hashtable h1 = (Hashtable) hashtable.get( "s1" );

        assertEquals( 3, h1.size() );

        assertProperty( "foo", "trygve", h1 );

        assertProperty( "bar", "24", h1 );

        assertProperty( "bool", "true", h1 );

        assertTrue( hashtable.containsKey( "s2" ) );

        Hashtable h2 = (Hashtable) hashtable.get( "s2" );

        assertEquals( 3, h2.size() );

        assertProperty( "foo", "anne", h2 );

        assertProperty( "bar", "22", h2 );

        assertProperty( "bool", "false", h2 );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private void assertProperty( String name, String value, Hashtable hashtable )
    {
        assertTrue( "Missing element '" + name + "'.", hashtable.containsKey( name ) );

        assertEquals( "Value for element '" + name + "' isn't the expected value.", value, hashtable.get( name ) );
    }
}
