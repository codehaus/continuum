package org.codehaus.continuum.project;

/*
 * LICENSE
 */

import java.io.Serializable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumBuildResult.java,v 1.1 2004-07-27 00:06:03 trygvis Exp $
 */
public interface ContinuumBuildResult
    extends Serializable
{
    ContinuumBuild getBuild();

    boolean isSuccess();
}
