package org.codehaus.plexus.continuum.trigger;

import org.codehaus.plexus.continuum.Continuum;
import org.codehaus.plexus.logging.AbstractLogEnabled;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: AbstractContinuumTrigger.java,v 1.1 2004-01-18 07:27:27 jvanzyl Exp $
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
