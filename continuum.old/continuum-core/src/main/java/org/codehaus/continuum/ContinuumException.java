package org.codehaus.continuum;

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
