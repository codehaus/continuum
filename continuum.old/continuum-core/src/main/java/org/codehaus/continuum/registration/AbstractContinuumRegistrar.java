package org.codehaus.continuum.registration;

import org.codehaus.continuum.Continuum;
import org.codehaus.plexus.logging.AbstractLogEnabled;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: AbstractContinuumRegistrar.java,v 1.4 2004-05-13 17:48:17 trygvis Exp $
 */
public abstract class AbstractContinuumRegistrar
    extends AbstractLogEnabled
    implements ContinuumRegistrar
{
    private Continuum continuum;

    public Continuum getContinuum()
    {
        return continuum;
    }
}
