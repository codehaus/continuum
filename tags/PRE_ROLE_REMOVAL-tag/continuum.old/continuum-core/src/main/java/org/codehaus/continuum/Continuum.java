package org.codehaus.plexus.continuum;

import org.apache.maven.Model;

/**
 * @author <a href="mailto:jason@plexus.org">Jason van Zyl</a>
 * @version $Id: Continuum.java,v 1.2 2003-10-09 04:00:38 pdonald Exp $
 */
public interface Continuum
{
    /** role of component. */
    String ROLE = Continuum.class.getName();

    void addModel( Model model );
}

