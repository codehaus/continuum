package org.codehaus.continuum.notification;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.project.BuildResult;

/*
 * LICENSE
 */

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: NotifierManager.java,v 1.1 2004-07-07 02:34:34 trygvis Exp $
 */
public interface NotifierManager
{
    String ROLE = NotifierManager.class.getName();

    void buildStarted( BuildResult build );

    void checkoutStarted( BuildResult build );
    
    /**
     * This method is called upon a completed checkout. If a error ocurred
     * <code>ex</code> will be non-null.
     * 
     * @param project The whose projects checkout completed.
     * @param ex Possibly a exception.
     * @throws ContinuumException
     */
    void checkoutComplete( BuildResult build, Exception ex );    

    void runningGoals( BuildResult build );
    
    void goalsCompleted( BuildResult build, Exception ex );
    
    void buildComplete( BuildResult build, Exception ex );
}
