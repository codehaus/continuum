package org.codehaus.plexus.continuum;

import org.apache.maven.Model;


/**
 *
 *
 * @author <a href="mailto:jason@plexus.org">Jason van Zyl</a>
 *
 * @version $Id: Continuum.java,v 1.1.1.1 2003-09-01 16:06:00 jvanzyl Exp $
 */
public interface Continuum
{
    /** role of component. */
    static String ROLE = Continuum.class.getName();

    public void addModel( Model model );
}

