package org.codehaus.continuum.project;

/*
 * LICENSE
 */

import java.util.Date;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: BuildResult.java,v 1.1 2004-06-27 22:20:27 trygvis Exp $
 */
public class BuildResult
{
    /** */
    public final static int BUILD_RESULT_OK = 1;

    /** */
    public final static int BUILD_RESULT_FAILURE = 2;

    /** */
    public final static int BUILD_RESULT_ERROR = 3;

    /** */
    private ContinuumProject project;

    /** */
    private Date timestamp;

    /** */
    private Throwable error;

    /**
     */
    public BuildResult()
    {
    }

    /**
     * @return Returns the project.
     */
    public ContinuumProject getProject()
    {
        return project;
    }

    /**
     * @param project The project to set.
     */
    public void setProject(ContinuumProject project)
    {
        this.project = project;
    }

    /**
     * @return Returns the timestamp.
     */
    public Date getTimestamp()
    {
        return timestamp;
    }

    /**
     * @param timestamp The timestamp to set.
     */
    public void setTimestamp(Date timestamp)
    {
        this.timestamp = timestamp;
    }

    /**
     * @return Returns the error.
     */
    public Throwable getError()
    {
        return error;
    }

    /**
     * @param error The error to set.
     */
    public void setError(Throwable error)
    {
        this.error = error;
    }
}
