package org.codehaus.plexus.continuum.projectstorage;

/*
 * LICENSE
 */

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumStoreException.java,v 1.2 2004-04-22 20:03:41 trygvis Exp $
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
