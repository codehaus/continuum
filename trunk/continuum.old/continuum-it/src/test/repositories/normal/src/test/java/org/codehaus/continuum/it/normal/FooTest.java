package org.codehaus.continuum.it.normal;

import junit.framework.TestCase;

public class FooTest
    extends TestCase
{
    public void testFoo()
    {
        Foo foo = new Foo();

        String result = foo.poke();

        assertEquals( "Hello World!", result );
    }
}
