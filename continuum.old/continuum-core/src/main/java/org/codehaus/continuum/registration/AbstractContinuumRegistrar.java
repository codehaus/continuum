package org.codehaus.plexus.continuum.registration;

import org.codehaus.plexus.continuum.Continuum;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: AbstractContinuumRegistrar.java,v 1.1 2004-01-18 18:01:40 jvanzyl Exp $
 */
public class AbstractContinuumRegistrar
{
    private Continuum continuum;

    public Continuum getContinuum()
    {
        return continuum;
    }
}
