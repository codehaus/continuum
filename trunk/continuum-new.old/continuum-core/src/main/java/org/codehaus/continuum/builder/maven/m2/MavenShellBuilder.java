package org.codehaus.continuum.builder.maven.m2;

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
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.builder.shell.ShellBuilder;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.Arrays;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: MavenShellBuilder.java,v 1.8 2005-03-28 15:35:48 trygvis Exp $
 */
public class MavenShellBuilder
    extends ShellBuilder
{
    public final static String CONFIGURATION_GOALS = "goals";

    /** @requirement */
    private MavenBuilderHelper builderHelper;

    /** @configuration */
    private String executable;

    /** @configuration */
    private String arguments;

    public ContinuumProject createProjectFromMetadata( URL metadata )
        throws ContinuumException
    {
        return builderHelper.createProjectFromMetadata( metadata );
    }

    public void updateProjectFromCheckOut( File workingDirectory, ContinuumProject project )
        throws ContinuumException
    {
        builderHelper.updateProjectFromMetadata( workingDirectory, project );
    }

    protected String getExecutable( ContinuumProject project )
        throws ContinuumException
    {
        return executable;
    }

    protected String[] getArguments( ContinuumProject project )
        throws ContinuumException
    {
        String[] a = splitAndTrimString( this.arguments, " " );

        String[] goals = getConfigurationStringArray( project.getConfiguration(), CONFIGURATION_GOALS, "," );

        String[] arguments = new String[ a.length + goals.length ];

        System.arraycopy( a, 0, arguments, 0, a.length );

        System.arraycopy( goals, 0, arguments, a.length, goals.length );

        System.err.println( "arguments: " + Arrays.asList( arguments ) );

        return arguments;
    }
}
