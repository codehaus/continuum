package org.codehaus.continuum.maven2;

/*
 * LICENSE
 */

import java.net.URL;

import org.codehaus.continuum.ContinuumException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: Maven2Utils.java,v 1.1 2004-10-20 17:02:45 trygvis Exp $
 */
public interface Maven2Utils
{
    String ROLE = Maven2Utils.class.getName();

    String addProjectFromUrl( URL url )
		throws ContinuumException;
}
