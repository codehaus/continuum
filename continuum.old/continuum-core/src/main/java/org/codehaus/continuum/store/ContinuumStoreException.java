package org.codehaus.continuum.projectstorage;

/*
 * LICENSE
 */

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumStoreException.java,v 1.4 2004-06-27 23:21:03 trygvis Exp $
 */
public class ContinuumProjectStorageException extends Exception
{
    public ContinuumProjectStorageException( String msg )
    {
        super( msg );
    }

    public ContinuumProjectStorageException( String msg, Exception ex )
    {
        super( msg, ex );
    }
}
