package org.codehaus.continuum.project;

/*
 * LICENSE
 */

import org.codehaus.continuum.project.AbstractProjectDescriptor;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: TestProjectDescriptor.java,v 1.2 2004-07-19 16:54:47 trygvis Exp $
 */
public class TestProjectDescriptor
    extends AbstractProjectDescriptor
{
    private int attribute;

    public void setAttribute( int attribute )
    {
        this.attribute = attribute;
    }

    public int getAttribute()
    {
        return attribute;
    }
}
