package org.codehaus.continuum.store;

/*
 * LICENSE
 */

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumStoreException.java,v 1.1 2004-07-20 18:26:16 trygvis Exp $
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
