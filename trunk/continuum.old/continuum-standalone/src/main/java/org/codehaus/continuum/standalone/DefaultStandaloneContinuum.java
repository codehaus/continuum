package org.codehaus.continuum.standalone;

/*
 * LICENSE
 */

import org.codehaus.continuum.Continuum;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultStandaloneContinuum.java,v 1.1 2004-06-27 19:28:46 trygvis Exp $
 */
public class DefaultStandaloneContinuum
    extends AbstractLogEnabled
    implements Startable
{
    private Continuum continuum;

    public void start()
        throws Exception
    {
    }

    public void stop()
        throws Exception
    {
    }
}
