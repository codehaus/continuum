package org.codehaus.continuum.utils;

import org.codehaus.plexus.configuration.PlexusConfigurationException;

/*
 * LICENSE
 */

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: PlexusUtils.java,v 1.1 2004-06-27 19:28:43 trygvis Exp $
 */
public class PlexusUtils
{
    private PlexusUtils()
    {
    }

    public static void assertConfiguration( Object configuration, String name )
        throws PlexusConfigurationException
    {
        if( configuration == null )
            throw new PlexusConfigurationException( "Missing configuration element: '" + name + "'." );

        if ( configuration instanceof String )
        {
            String str = (String)configuration;

            if ( str.trim().length() == 0 )
                throw new PlexusConfigurationException( "Misconfigured element '" + name + "': Element cannot be empty." );
        }
    }

    public static void assertRequirement( Object requirement, Class clazz )
        throws PlexusConfigurationException
    {
        if ( requirement == null )
            throw new PlexusConfigurationException( "Missing requirement '" + clazz.getName() + "'." );
    }
}
