package org.codehaus.continuum.project;

/*
 * LICENSE
 */

import java.io.Serializable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumProject.java,v 1.4 2004-07-07 02:34:35 trygvis Exp $
 */
public interface ContinuumProject
    extends Serializable
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

    /**
     * @return Returns the id.
     */
    public String getId();

    /**
     * @param id The id to set.
     */
    public void setId( String id );

    /**
     * @return Returns the type.
     */
    public String getType();

    /**
     * @param type The type to set.
     */
    public void setType( String type );

    /**
     * @return Returns the name.
     */
    public String getName();

    /**
     * @param name The name to set.
     */
    public void setName( String name );

    /**
     * @return Returns the scm connection.
     */
    public String getScmConnection();

    /**
     * @param ccmConnection The ccmConnection to set.
     */
    public void setScmConnection( String scmConnection );

    /**
     * @return Returns the state.
     */
    public int getState();

    /**
     * @param state The state to set.
     */
    public void setState( int state );

    public ProjectDescriptor getDescriptor();

    public void setDescriptor( ProjectDescriptor descriptor );

//    public List getBuilds();
}
