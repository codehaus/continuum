package org.codehaus.plexus.continuum;

import org.codehaus.plexus.PlexusTestCase;

import java.io.InputStream;

public class DefaultContinuumTest
    extends PlexusTestCase
{
    private String mavenCoreUrl = "http://cvs.apache.org/viewcvs.cgi/*checkout*/maven-components/maven-core/project.xml";

    protected InputStream getCustomConfiguration()
    {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        InputStream is = cl.getResourceAsStream( "org/codehaus/plexus/continuum/plexus.xml" );

        return is;
    }

    public void testContinuum()
        throws Exception
    {
        Continuum continuum = (Continuum) lookup( Continuum.ROLE );

        assertNotNull( continuum );
/*
        continuum.addProject( mavenCoreUrl );
        continuum.buildProjects();
*/
    }
}
