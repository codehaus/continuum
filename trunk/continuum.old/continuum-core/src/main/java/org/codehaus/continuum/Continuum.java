package org.codehaus.plexus.continuum;

import org.apache.maven.Model;

/**
 * @author <a href="mailto:jason@plexus.org">Jason van Zyl</a>
 * @version $Id: Continuum.java,v 1.3 2003-10-12 00:55:39 pdonald Exp $
 */
public interface Continuum
{
    void addModel( Model model );
}

