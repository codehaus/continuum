package org.codehaus.continuum.notification;

import org.apache.maven.project.MavenProject;

import org.codehaus.continuum.ContinuumException;

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
 * @version $Id: ContinuumNotifier.java,v 1.3 2004-05-13 17:48:17 trygvis Exp $
 */
public interface ContinuumNotifier
{
    static String ROLE = ContinuumNotifier.class.getName();

    void buildStarted( MavenProject project )
        throws ContinuumException;

    void checkoutStarted( MavenProject project )
        throws ContinuumException;

    /**
     * This method is called upon a completed checkout. If a error ocurred
     * <code>ex</code> will be non-null.
     * 
     * @param project The whose projects checkout completed.
     * @param ex Possibly a exception.
     * @throws ContinuumException
     */
    void checkoutComplete( MavenProject project, Exception ex )
        throws ContinuumException;

    void runningGoals( MavenProject project )
        throws ContinuumException;

    void goalsCompleted( MavenProject project, Exception ex )
        throws ContinuumException;

    void buildComplete( MavenProject project, Exception ex )
        throws ContinuumException;
}
