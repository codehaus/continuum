package org.codehaus.continuum.web.context;

import java.util.List;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: View.java,v 1.1 2005-03-20 07:19:13 jvanzyl Exp $
 */
public class View
{
    private String id;

    private List scalars;

    public String getId()
    {
        return id;
    }

    public List getScalars()
    {
        return scalars;
    }
}
