package org.codehaus.continuum.builder;

/*
 * LICENSE
 */

import java.io.File;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumBuildResult;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ProjectDescriptor;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumBuilder.java,v 1.2 2004-07-27 00:06:02 trygvis Exp $
 */
public interface ContinuumBuilder
{
    String ROLE = ContinuumBuilder.class.getName();

    ProjectDescriptor createDescriptor( ContinuumProject project )
        throws ContinuumException;

    ContinuumBuildResult build( File workingDirectory, ContinuumBuild build )
        throws ContinuumException;
}
