package org.codehaus.continuum;

/*
 * The MIT License
 *
 * Copyright (c) 2004, Jason van Zyl and Trygve Laugst�l
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

import org.codehaus.continuum.project.ContinuumProject;

import java.net.URL;
import java.util.Iterator;

/**
 * This is the main entry point for Continuum. Projects are added to Continuum
 * by providing an URL to the metadata for project. The metadata for a project
 * must contain the following information:
 * <p/>
 * o project name
 * o project id
 * o SCM information
 * o email notification list
 * o project developers
 */
public interface Continuum
{
    String ROLE = Continuum.class.getName();

    String addProject( URL url, String builder )
        throws ContinuumException;

    void removeProject( String projectId )
        throws ContinuumException;

    void updateProject( String projectId )
        throws ContinuumException;

    ContinuumProject getProject( String projectId )
        throws ContinuumException;

    Iterator getAllProjects( int start, int end )
        throws ContinuumException;

    String buildProject( String projectId )
        throws ContinuumException;

    boolean checkIfProjectNeedsToBeBuilt( String projectId )
        throws ContinuumException;

    int getBuildQueueLength()
        throws ContinuumException;
}
