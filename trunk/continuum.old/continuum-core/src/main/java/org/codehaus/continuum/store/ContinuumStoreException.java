package org.codehaus.continuum.store;

/*
 * LICENSE
 */

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumStoreException.java,v 1.5 2004-07-01 15:30:58 trygvis Exp $
 */
public class ContinuumStoreException extends Exception
{
    public ContinuumStoreException( String msg )
    {
        super( msg );
    }

    public ContinuumStoreException( String msg, Exception ex )
    {
        super( msg, ex );
    }
}
