package org.codehaus.continuum.xmlrpc;

/*
 * LICENSE
 */

import org.codehaus.continuum.Continuum;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumXmlRpcServer.java,v 1.1 2004-07-07 03:21:48 trygvis Exp $
 */
public interface ContinuumXmlRpcServer
    extends Continuum
{
    String ROLE = ContinuumXmlRpcServer.class.getName();
}
