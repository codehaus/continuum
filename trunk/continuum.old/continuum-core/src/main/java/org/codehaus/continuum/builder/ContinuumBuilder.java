package org.codehaus.continuum.builder;

/*
 * LICENSE
 */

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumBuilder.java,v 1.5 2004-07-01 15:30:56 trygvis Exp $
 */
public interface ContinuumBuilder
{
    String ROLE = ContinuumBuilder.class.getName();

    void build( String buildId );
}
