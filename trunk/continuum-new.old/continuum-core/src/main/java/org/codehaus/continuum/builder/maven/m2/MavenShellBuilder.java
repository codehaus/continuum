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

import java.io.File;
import java.net.URL;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: MavenShellBuilder.java,v 1.4 2005-03-10 00:05:50 trygvis Exp $
 */
public class MavenShellBuilder
    extends ShellBuilder
{
    /** @requirement */
    private MavenBuilderHelper builderHelper;

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
}