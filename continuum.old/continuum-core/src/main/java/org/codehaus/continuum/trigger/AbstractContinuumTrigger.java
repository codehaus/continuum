package org.codehaus.continuum.trigger;

import org.codehaus.continuum.Continuum;
import org.codehaus.plexus.logging.AbstractLogEnabled;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: AbstractContinuumTrigger.java,v 1.2 2004-05-13 17:48:17 trygvis Exp $
 */
public class AbstractContinuumTrigger
    extends AbstractLogEnabled
    implements ContinuumTrigger
{
    private Continuum continuum;

    public Continuum getContinuum()
    {
        return continuum;
    }
}
