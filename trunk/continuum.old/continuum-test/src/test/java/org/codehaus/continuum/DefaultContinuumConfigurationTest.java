package org.codehaus.plexus.continuum;

/*
 * LISENCE
 */

import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultContinuumConfigurationTest.java,v 1.1 2004-04-07 15:56:56 trygvis Exp $
 */
public class DefaultContinuumConfigurationTest
    extends PlexusTestCase
{
    private String mavenCoreUrl = "http://cvs.apache.org/viewcvs.cgi/*checkout*/maven-components/maven-core/project.xml";
    private String commonsLangUrl = "http://cvs.apache.org/viewcvs.cgi/*checkout*/jakarta-commons/lang/project.xml";
    private String commonsLoggingUrl = "http://cvs.apache.org/viewcvs.cgi/*checkout*/jakarta-commons/logging/project.xml";

    /**
     * This test tests the default configuration of the continuum server.
     */
    public void testConfiguration()
        throws Exception
    {
        ;
    }
}
