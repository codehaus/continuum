package org.codehaus.continuum.scm;

/*
 * LICENSE
 */

import java.io.File;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.project.ContinuumProject;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumScm.java,v 1.2 2004-07-27 00:06:03 trygvis Exp $
 */
public interface ContinuumScm
{
    String ROLE = ContinuumScm.class.getName();

    void clean( ContinuumProject project )
        throws ContinuumException;

    File checkout( ContinuumProject project )
        throws ContinuumException;

    File update( ContinuumProject project )
        throws ContinuumException;
}
