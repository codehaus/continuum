package org.codehaus.continuum.it.it1;

/*
 * LICENSE
 */

import org.codehaus.continuum.store.prevayler.PrevaylerContinuumStore;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: PrevaylerMaven2Test.java,v 1.1 2004-10-06 14:27:04 trygvis Exp $
 */
public class PrevaylerMaven2Test
    extends AbstractMaven2Test
{
    public PrevaylerMaven2Test()
    {
        super( PrevaylerContinuumStore.class );
    }

    public void setUpStore()
        throws Exception
    {
        getContinuumStore().createDatabase();
    }

    public void tearDownStore()
        throws Exception
    {
        getContinuumStore().deleteDatabase();
    }
}
