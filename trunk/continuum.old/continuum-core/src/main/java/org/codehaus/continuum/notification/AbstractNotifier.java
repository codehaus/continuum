package org.codehaus.continuum.notification;

/*
 * LICENSE
 */

import java.util.Iterator;
import java.util.List;

import org.apache.maven.project.MavenProject;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AbstractNotifier.java,v 1.2 2004-05-13 17:48:17 trygvis Exp $
 */
public class AbstractNotifier
{
    protected String createMessage( List messages, MavenProject project )
    {
        StringBuffer message = new StringBuffer();

        // Notification is there are failures.
        if ( messages.size() > 0 )
        {
            for ( Iterator j = messages.iterator(); j.hasNext(); )
            {
                message.append( j.next() ).append( "\n" );
            }
        }
        else
        {
            message.append( "Build OK." );
        }

        return message.toString();
    }
}
