package org.codehaus.plexus.continuum.projectstorage;

/*
 * LISENCE
 */

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumStoreException.java,v 1.1 2004-04-07 15:56:56 trygvis Exp $
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
