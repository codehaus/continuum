package org.codehaus.continuum.scm;

/*
 * LICENSE
 */

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.project.ContinuumProject;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumScm.java,v 1.1 2004-07-20 18:26:16 trygvis Exp $
 */
public interface ContinuumScm
{
    String ROLE = ContinuumScm.class.getName();

    void clean( ContinuumProject project )
        throws ContinuumException;

    String checkout( ContinuumProject project )
        throws ContinuumException;

    String update( ContinuumProject project )
        throws ContinuumException;
}
