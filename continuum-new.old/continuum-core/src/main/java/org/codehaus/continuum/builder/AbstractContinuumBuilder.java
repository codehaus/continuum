package org.codehaus.continuum.builder;

/*
 * Copyright 2004-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.codehaus.continuum.ContinuumException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AbstractContinuumBuilder.java,v 1.5 2005-03-28 14:10:57 trygvis Exp $
 */
public abstract class AbstractContinuumBuilder
    extends AbstractLogEnabled
    implements ContinuumBuilder
{
    protected String getConfigurationString( Properties configuration, String property )
        throws ContinuumException
    {
        String string = configuration.getProperty( property );

        if ( StringUtils.isEmpty( string ) )
        {
            throw new ContinuumException( "Missing configuration: '" + property + "'." );
        }

        return string;
    }

    protected String getConfigurationString( Properties configuration, String property, String defaultValue )
        throws ContinuumException
    {
        String string = configuration.getProperty( property );

        if ( StringUtils.isEmpty( string ) )
        {
            return defaultValue;
        }

        return string;
    }

    protected String[] getConfigurationStringArray( Properties configuration, String property, String separator )
        throws ContinuumException
    {
        String value = getConfigurationString( configuration, property );

        String[] array = StringUtils.split( value, separator );

        for ( int i = 0; i < array.length; i++ )
        {
            array[ i ] = array[ i ].trim();
        }

        return array;
    }

    protected String[] getConfigurationStringArray( Properties configuration, String property, String separator, String[] defaultValue )
        throws ContinuumException
    {
        String value = getConfigurationString( configuration, property, null );

        if ( value == null )
        {
            return defaultValue;
        }

        String[] array = StringUtils.split( value, separator );

        for ( int i = 0; i < array.length; i++ )
        {
            array[ i ] = array[ i ].trim();
        }

        return array;
    }

    protected static File createMetadataFile( URL metadata )
        throws ContinuumException
    {
        try
        {
            InputStream is = metadata.openStream();

            File file = File.createTempFile( "continuum", "tmp" );

            FileWriter writer = new FileWriter( file );

            IOUtil.copy( is, writer );

            is.close();

            writer.close();

            return file;
        }
        catch ( Exception e )
        {
            throw new ContinuumException( "Cannot create metadata file:", e );
        }
    }
}
