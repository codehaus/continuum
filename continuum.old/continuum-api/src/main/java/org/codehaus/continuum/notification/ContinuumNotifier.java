package org.codehaus.continuum.notification;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.project.ContinuumBuild;

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
 * @version $Id: ContinuumNotifier.java,v 1.2 2004-07-27 00:06:02 trygvis Exp $
 */
public interface ContinuumNotifier
{
    static String ROLE = ContinuumNotifier.class.getName();

    void buildStarted( ContinuumBuild build )
        throws ContinuumException;

    void checkoutStarted( ContinuumBuild build )
        throws ContinuumException;

    /**
     * This method is called upon a completed checkout. If a error ocurred
     * <code>ex</code> will be non-null.
     * 
     * @param project The whose projects checkout completed.
     * @param ex Possibly a exception.
     * @throws ContinuumException
     */
    void checkoutComplete( ContinuumBuild build )
        throws ContinuumException;

    void runningGoals( ContinuumBuild build )
        throws ContinuumException;

    void goalsCompleted( ContinuumBuild build )
        throws ContinuumException;

    void buildComplete( ContinuumBuild build )
        throws ContinuumException;
}
