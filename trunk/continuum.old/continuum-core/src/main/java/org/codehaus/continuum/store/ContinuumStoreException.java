package org.codehaus.continuum.projectstorage;

/*
 * LICENSE
 */

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumStoreException.java,v 1.3 2004-05-13 17:48:17 trygvis Exp $
 */
public class ProjectStorageException extends Exception
{
    public ProjectStorageException( String msg )
    {
        super( msg );
    }

    public ProjectStorageException( String msg, Exception ex )
    {
        super( msg, ex );
    }
}
