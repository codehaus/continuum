package org.codehaus.continuum.xmlrpc;

/*
 * LICENSE
 */

import org.codehaus.continuum.Continuum;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumXmlRpcInterface.java,v 1.2 2004-07-07 02:34:42 trygvis Exp $
 */
public interface ContinuumXmlRpcInterface
    extends Continuum
{
    String ROLE = ContinuumXmlRpcInterface.class.getName();
/*
    String registerProject( String name, String scmConnection, String type )
        throws ContinuumException;
*/
}
