package org.codehaus.continuum.builder.maven2;

/*
 * LICENSE
 */

import java.util.ArrayList;
import java.util.List;

import org.codehaus.continuum.project.AbstractProjectDescriptor;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: Maven2ProjectDescriptor.java,v 1.1 2004-07-07 02:34:34 trygvis Exp $
 */
public class Maven2ProjectDescriptor
    extends AbstractProjectDescriptor
{
    /** */
    private List goals;

    /**
     * @return Returns the goals.
     */
    public List getGoals()
    {
        if ( goals == null )
        {
            goals = new ArrayList();
        }

        return goals;
    }

    /**
     * @param goals The goals to set.
     */
    public void setGoals( List goals )
    {
        this.goals = goals;
    }
}
