package org.codehaus.continuum.project;

/*
 * LICENSE
 */


/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumProject.java,v 1.3 2004-07-03 03:21:15 trygvis Exp $
 */
public class ContinuumProject
{
    /**
     * This state indicates that the project is new and has never been build.
     */
    public final static int PROJECT_STATE_NEW = 1;

    /**
     * This state indicates that the project has been successfully build.
     */
    public final static int PROJECT_STATE_OK = 2;

    /**
     * This state indicates that the project didn't build successfully.
     */
    public final static int PROJECT_STATE_FAILED = 3;

    /**
     * This stats indicates that there was a error while building the project.
     *
     * A error while building the project might indicate that it couldn't
     * download the sources or other things that continuum doesn't have any
     * control over.
     */
    public final static int PROJECT_STATE_ERROR = 4;

    /**
     * This state indicates that this project has been placed on the build queue.
     *
     * Continuum can be configured with a delay from the first build signal to 
     * the actual build starts to make. 
     */
    public final static int PROJECT_STATE_BUILD_SIGNALED = 5;

    /**
     * This state indicates that a project is currently beeing build.
     */
    public final static int PROJECT_STATE_BUILDING = 6;

    /** */
    private String id;

    /** */
    private String name;

    /** */
    private String scmConnection;

    /** */
    private int projectState;

    /** */
    private Object projectDescriptor;

    /**
     */
    public ContinuumProject( String id )
    {
        this.id = id;
    }

    /**
     * @return Returns the id.
     */
    public String getId()
    {
        return id;
    }

    /**
     * @param id The id to set.
     */
    public void setId( String id )
    {
        this.id = id;
    }

    /**
     * @return Returns the name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName( String name )
    {
        this.name = name;
    }

    /**
     * @return Returns the scm connection.
     */
    public String getScmConnection()
    {
        return scmConnection;
    }

    /**
     * @param ccmConnection The ccmConnection to set.
     */
    public void setScmConnection( String scmConnection)
    {
        this.scmConnection = scmConnection;
    }

    /**
     * @return Returns the projectState.
     */
    public int getProjectState()
    {
        return projectState;
    }

    /**
     * @param projectState The projectState to set.
     */
    public void setProjectState( int projectState )
    {
        this.projectState = projectState;
    }

    public Object getProjectDescriptor()
    {
        return projectDescriptor;
    }

    public void setProjectDescriptor( Object projectDescriptor )
    {
        this.projectDescriptor = projectDescriptor;
    }
}
