package org.codehaus.continuum.store;

/*
 * LICENSE
 */

import org.codehaus.continuum.project.GenericContinuumBuild;
import org.codehaus.continuum.project.GenericContinuumProject;
import org.codehaus.plexus.logging.AbstractLogEnabled;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AbstractContinuumStore.java,v 1.2 2004-07-27 00:06:03 trygvis Exp $
 */
public abstract class AbstractContinuumStore
    extends AbstractLogEnabled
    implements ContinuumStore
{
    protected GenericContinuumProject getGenericProject( String id )
        throws ContinuumStoreException
    {
        return (GenericContinuumProject) getProject( id );
    }

    protected GenericContinuumBuild getGenericBuild( String id )
        throws ContinuumStoreException
    {
        return (GenericContinuumBuild) getBuild( id );
    }
}
