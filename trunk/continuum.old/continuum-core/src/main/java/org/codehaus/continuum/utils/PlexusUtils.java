package org.codehaus.continuum.utils;

/*
 * LICENSE
 */

import org.codehaus.plexus.configuration.PlexusConfigurationException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: PlexusUtils.java,v 1.2 2004-07-01 15:30:58 trygvis Exp $
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
        {
            throw new PlexusConfigurationException( "Missing configuration element: '" + name + "'." );
        }

        if ( configuration instanceof String )
        {
            String str = (String)configuration;

            if ( str.trim().length() == 0 )
            {
                throw new PlexusConfigurationException( "Misconfigured element '" + name + "': Element cannot be empty." );
            }
        }
    }

    public static void assertRequirement( Object requirement, String role )
        throws PlexusConfigurationException
    {
        if ( requirement == null )
        {
            throw new PlexusConfigurationException( "Missing requirement '" + role + "'." );
        }
    }
}
