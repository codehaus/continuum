package org.codehaus.continuum;

/*
 * LICENSE
 */

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumException.java,v 1.1 2004-07-20 18:25:57 trygvis Exp $
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
