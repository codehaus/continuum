package org.codehaus.continuum;

/*
 * LICENSE
 */

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumException.java,v 1.5 2004-06-27 23:21:03 trygvis Exp $
 */
public class ContinuumException extends Exception
{
    public ContinuumException( String message )
    {
        super( message );
    }

    public ContinuumException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
