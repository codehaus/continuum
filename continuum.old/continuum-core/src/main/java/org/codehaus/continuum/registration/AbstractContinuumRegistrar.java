package org.codehaus.plexus.continuum.registration;

import org.codehaus.plexus.continuum.Continuum;
import org.codehaus.plexus.logging.AbstractLogEnabled;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: AbstractContinuumRegistrar.java,v 1.2 2004-01-18 18:23:03 jvanzyl Exp $
 */
public class AbstractContinuumRegistrar
    extends AbstractLogEnabled
{
    private Continuum continuum;

    public Continuum getContinuum()
    {
        return continuum;
    }
}
