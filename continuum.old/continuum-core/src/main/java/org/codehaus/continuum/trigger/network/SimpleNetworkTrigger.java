package org.codehaus.plexus.continuum.trigger.network;

import java.io.InputStream;
import java.io.OutputStream;

import org.codehaus.plexus.continuum.network.ConnectionConsumer;
import org.codehaus.plexus.continuum.network.NetworkUtils;
import org.codehaus.plexus.continuum.trigger.AbstractContinuumTrigger;

/**
 * This trigger listens on a specified port and takes one line
 * of input which contains the the groupId and artifactId of the
 * project to build or the special word "all" to indicate building
 * all the projects.
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: SimpleNetworkTrigger.java,v 1.2 2004-04-07 15:56:56 trygvis Exp $
 */
public class SimpleNetworkTrigger
    extends AbstractContinuumTrigger
    implements ConnectionConsumer
{
    // ----------------------------------------------------------------------
    // ConnectionConsumer Implementation
    // ----------------------------------------------------------------------

    public void consumeConnection( InputStream input, OutputStream output )
    {
        NetworkUtils.closeInput( input );
        NetworkUtils.closeOutput( output );

        try
        {
            getContinuum().buildProjects();
        }
        catch( Exception ex )
        {
            
        }
    }
}
