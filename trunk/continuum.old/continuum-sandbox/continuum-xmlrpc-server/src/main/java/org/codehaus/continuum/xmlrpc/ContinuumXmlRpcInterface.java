package org.codehaus.continuum.xmlrpc;

/*
 * LICENSE
 */

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumXmlRpcInterface.java,v 1.1 2004-06-27 19:28:46 trygvis Exp $
 */
public interface ContinuumXmlRpcInterface
{
    String registerProject( String pom )
        throws Exception;
}
