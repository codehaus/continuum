package org.codehaus.continuum.web.model;

/*
 * LICENSE
 */

import java.util.List;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: Maven2ProjectDescriptorModel.java,v 1.1 2004-07-27 05:16:12 trygvis Exp $
 */
public class Maven2ProjectDescriptorModel
    implements ProjectDescriptorModel
{
    private List goals;

    public Maven2ProjectDescriptorModel( List goals )
    {
        this.goals = goals;
    }

    /**
     * @return Returns the goals.
     */
    public List getGoals()
    {
        return goals;
    }
}
