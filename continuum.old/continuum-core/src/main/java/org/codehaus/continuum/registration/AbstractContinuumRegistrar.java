package org.codehaus.plexus.continuum.registration;

import org.codehaus.plexus.continuum.Continuum;
import org.codehaus.plexus.logging.AbstractLogEnabled;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: AbstractContinuumRegistrar.java,v 1.3 2004-04-07 15:56:56 trygvis Exp $
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
