package org.codehaus.continuum;

/*
 * LICENSE
 */

import org.codehaus.continuum.project.ContinuumProject;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumBuilder.java,v 1.3 2004-06-27 22:20:27 trygvis Exp $
 */
public interface ContinuumBuilder
{
    void build( String projectId );
}
