package org.codehaus.continuum.builder;

/*
 * LICENSE
 */

import org.codehaus.continuum.ContinuumException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: BuilderManager.java,v 1.1 2004-07-07 02:34:34 trygvis Exp $
 */
public interface BuilderManager
{
    String ROLE = BuilderManager.class.getName();

    ContinuumBuilder getBuilderForProject( String projectId )
        throws ContinuumException;

    ContinuumBuilder getBuilderForBuild( String buildId )
        throws ContinuumException;
}
