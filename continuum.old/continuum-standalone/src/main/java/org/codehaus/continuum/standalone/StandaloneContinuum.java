package org.codehaus.continuum.standalone;

/*
 * LICENSE
 */

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: StandaloneContinuum.java,v 1.1 2004-06-27 19:28:46 trygvis Exp $
 */
public interface StandaloneContinuum
{
    void start()
        throws Exception;

    void stop()
        throws Exception;
}
