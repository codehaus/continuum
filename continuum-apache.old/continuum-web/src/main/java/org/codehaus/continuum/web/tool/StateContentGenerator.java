package org.codehaus.continuum.web.tool;

import org.codehaus.plexus.formica.web.ContentGenerator;
import org.apache.maven.continuum.project.ContinuumProject;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: StateContentGenerator.java,v 1.1 2005-04-04 14:05:38 jvanzyl Exp $
 */
public class StateContentGenerator
    implements ContentGenerator
{
    public String generate( Object item )
    {
        ContinuumProject p = (ContinuumProject) item;

        return null;
    }
}
