package org.codehaus.continuum.project;

/*
 * LICENSE
 */

import org.codehaus.continuum.project.AbstractProjectDescriptor;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: MockProjectDescriptor.java,v 1.1 2004-07-07 02:34:38 trygvis Exp $
 */
public class MockProjectDescriptor
    extends AbstractProjectDescriptor
{
    private int attribute;

    public MockProjectDescriptor()
    {
    }

    public void setAttribute( int attribute )
    {
        this.attribute = attribute;
    }

    public int getAttribute()
    {
        return attribute;
    }
}
