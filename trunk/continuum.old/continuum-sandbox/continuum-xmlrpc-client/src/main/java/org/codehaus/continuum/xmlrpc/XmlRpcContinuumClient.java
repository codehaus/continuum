package org.codehaus.continuum.xmlrpc;

/*
 * LICENSE
 */

import org.codehaus.continuum.Continuum;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: XmlRpcContinuumClient.java,v 1.1 2004-07-07 04:56:40 trygvis Exp $
 */
public interface XmlRpcContinuumClient
    extends Continuum
{
    String ROLE = XmlRpcContinuumClient.class.getName();

    void setHostname( String hostname );

    void setPort( String port );
}
