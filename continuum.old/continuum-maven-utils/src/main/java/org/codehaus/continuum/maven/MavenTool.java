package org.codehaus.continuum.maven;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.io.File;
import java.util.List;

import org.apache.maven.project.MavenProject;
import org.apache.maven.ExecutionResponse;

import org.codehaus.continuum.ContinuumException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: MavenTool.java,v 1.1.1.1 2004-10-28 16:08:17 trygvis Exp $
 */
public interface MavenTool
{
    String ROLE = MavenTool.class.getName();

    // ----------------------------------------------------------------------
    // MavenProject Related Methods
    // ----------------------------------------------------------------------

    MavenProject getProject( File file )
        throws ContinuumException;

    String getProjectName( MavenProject project );

    String getNagEmailAddress( MavenProject project );

    String getScmUrl( MavenProject project );

    String getVersion( MavenProject project );

    // ----------------------------------------------------------------------
    // Goal Execution
    // ----------------------------------------------------------------------

    ExecutionResponse execute( MavenProject project, List goals )
        throws ContinuumException;

    ExternalMavenExecutionResult executeExternal( File workingDirectory, MavenProject mavenProject, List goals )
        throws ContinuumException;
}
