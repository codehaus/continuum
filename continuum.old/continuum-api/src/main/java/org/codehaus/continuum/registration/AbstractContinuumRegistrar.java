package org.codehaus.continuum.registration;

import org.codehaus.continuum.Continuum;
import org.codehaus.plexus.logging.AbstractLogEnabled;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: AbstractContinuumRegistrar.java,v 1.1 2004-07-20 18:26:00 trygvis Exp $
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
