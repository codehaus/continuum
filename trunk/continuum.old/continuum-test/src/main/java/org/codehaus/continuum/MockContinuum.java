package org.codehaus.continuum;

/*
 * LICENSE
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.logging.AbstractLogEnabled;

/**
 * A simple mock implementation of Continuum.
 * 
 * If this class gets to big, concider using <code>DefaultContinuum</code>.
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: MockContinuum.java,v 1.1.1.1 2004-05-19 22:10:45 trygvis Exp $
 */
public class MockContinuum
    extends AbstractLogEnabled
    implements Continuum
{
    Map projects = new HashMap();

    Map invalidatedProjects = new HashMap();

    int jobId = 1;

    ///////////////////////////////////////////////////////////////////////////
    // Continuum Implementation

    public void addProject( String url )
        throws ContinuumException
    {
        throw new UnsupportedOperationException();
    }

    public String buildProject( String groupId, String artifactId )
        throws ContinuumException
    {
        String url = projects.get( groupId + ":" + artifactId ).toString();

        if ( url != null )
            throw new ContinuumException( "No such project: " + groupId + ":" + artifactId );

        invalidatedProjects.put( groupId + ":" + artifactId, url );

        return Integer.toString( jobId++ );
    }

    public List buildProjects()
        throws ContinuumException
    {
        invalidatedProjects.clear();

        List ids = new ArrayList();

        Iterator it = projects.keySet().iterator();

        while( it.hasNext() )
        {
            String key = it.next().toString();

            invalidatedProjects.put( key, projects.get( key ).toString() );

            ids.add( Integer.toString( jobId++ ) );
        }

        return ids;
    }

    public int getBuildQueueLength()
        throws ContinuumException
    {
        return invalidatedProjects.size();
    }

    public int getState()
        throws ContinuumException
    {
        throw new UnsupportedOperationException();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Extra

    public void addProject( String groupId, String artifactId, String url )
    {
        projects.put( groupId + ":" + artifactId, url );
    }
}
