package org.codehaus.continuum.notification;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.project.ContinuumBuild;

/*
 * LICENSE
 */

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: NotifierManager.java,v 1.2 2004-07-27 00:06:04 trygvis Exp $
 */
public interface NotifierManager
{
    String ROLE = NotifierManager.class.getName();

    void buildStarted( ContinuumBuild build );

    void checkoutStarted( ContinuumBuild build );

    /**
     * This method is called upon a completed checkout. If a error ocurred
     * <code>ex</code> will be non-null.
     * 
     * @param project The whose projects checkout completed.
     * @param ex Possibly a exception.
     * @throws ContinuumException
     */
    void checkoutComplete( ContinuumBuild build );    

    void runningGoals( ContinuumBuild build );

    void goalsCompleted( ContinuumBuild build );

    void buildComplete( ContinuumBuild build );
}
