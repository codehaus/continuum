package org.codehaus.continuum.notification;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.project.BuildResult;

/**
 * Approximate sequence:
 * <ul>
 *  <li>buildStarted
 *  <li>checkoutStarted
 *  <li>checkoutCompleted
 *  <li>runningGoals
 *  <li>goalsCompleted
 *  <li>buildCompleted
 * </ul>
 * If any of the steps has a non-null exception a error occurred and 
 * the sequence will abort.
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: ContinuumNotifier.java,v 1.4 2004-07-03 03:21:14 trygvis Exp $
 */
public interface ContinuumNotifier
{
    static String ROLE = ContinuumNotifier.class.getName();

    void buildStarted( BuildResult build )
        throws ContinuumException;

    void checkoutStarted( BuildResult build )
        throws ContinuumException;

    /**
     * This method is called upon a completed checkout. If a error ocurred
     * <code>ex</code> will be non-null.
     * 
     * @param project The whose projects checkout completed.
     * @param ex Possibly a exception.
     * @throws ContinuumException
     */
    void checkoutComplete( BuildResult build, Exception ex )
        throws ContinuumException;

    void runningGoals( BuildResult build )
        throws ContinuumException;

    void goalsCompleted( BuildResult build, Exception ex )
        throws ContinuumException;

    void buildComplete( BuildResult build, Exception ex )
        throws ContinuumException;
}
