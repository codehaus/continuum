package org.codehaus.continuum.trigger;

import org.codehaus.continuum.Continuum;
import org.codehaus.plexus.logging.AbstractLogEnabled;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: AbstractContinuumTrigger.java,v 1.1 2004-07-20 18:26:16 trygvis Exp $
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
