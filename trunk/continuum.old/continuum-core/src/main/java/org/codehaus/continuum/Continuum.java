package org.codehaus.plexus.continuum;

import org.apache.maven.Model;

/**
 * @author <a href="mailto:jason@plexus.org">Jason van Zyl</a>
 * @version $Id: Continuum.java,v 1.4 2003-10-12 01:14:55 pdonald Exp $
 */
public interface Continuum
{
    /** role of component. */
    String ROLE = Continuum.class.getName();

    void addModel( Model model );
}

