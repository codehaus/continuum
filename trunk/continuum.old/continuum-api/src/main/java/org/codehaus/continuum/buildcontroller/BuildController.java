package org.codehaus.continuum.buildcontroller;

/*
 * LICENSE
 */

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: BuildController.java,v 1.1 2004-10-24 14:15:58 trygvis Exp $
 */
public interface BuildController
{
    String ROLE = BuildController.class.getName();

    void build( String buildId );
}
