package org.codehaus.continuum.builder;

/*
 * LICENSE
 */

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ProjectDescriptor;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumBuilder.java,v 1.6 2004-07-07 02:34:34 trygvis Exp $
 */
public interface ContinuumBuilder
{
    String ROLE = ContinuumBuilder.class.getName();

    public ProjectDescriptor createDescriptor( ContinuumProject project )
        throws ContinuumException;

    void build( String buildId )
        throws ContinuumException;
}
