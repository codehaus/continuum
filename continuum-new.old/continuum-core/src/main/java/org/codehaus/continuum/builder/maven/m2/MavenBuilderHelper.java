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

import java.io.File;
import java.net.URL;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: MavenBuilderHelper.java,v 1.2 2005-03-10 00:05:50 trygvis Exp $
 */
public interface MavenBuilderHelper
{
    String ROLE = MavenBuilderHelper.class.getName();

    public ContinuumProject createProjectFromMetadata( URL metadata )
        throws ContinuumException;

    public void updateProjectFromMetadata( File workingDirectory, ContinuumProject project )
        throws ContinuumException;
}
