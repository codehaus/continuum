package org.codehaus.continuum.project;

/*
 * LICENSE
 */

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumProjectState.java,v 1.2 2004-07-27 00:06:03 trygvis Exp $
 */
public class ContinuumProjectState
{
    /**
     * This state indicates that the project is new and has never been build.
     */
    public final static ContinuumProjectState NEW = new ContinuumProjectState( "new" );

    /**
     * This state indicates that the project has been successfully build.
     */
    public final static ContinuumProjectState OK = new ContinuumProjectState( "ok" );

    /**
     * This state indicates that the project didn't build successfully.
     */
    public final static ContinuumProjectState FAILED = new ContinuumProjectState( "failed" );

    /**
     * This stats indicates that there was a error while building the project.
     *
     * A error while building the project might indicate that it couldn't
     * download the sources or other things that continuum doesn't have any
     * control over.
     */
    public final static ContinuumProjectState ERROR = new ContinuumProjectState( "error" );

    /**
     * This state indicates that this project has been placed on the build queue.
     *
     * Continuum can be configured with a delay from the first build signal to 
     * the actual build starts to make. 
     */
    public final static ContinuumProjectState BUILD_SIGNALED = new ContinuumProjectState( "signaled" );

    /**
     * This state indicates that a project is currently beeing build.
     */
    public final static ContinuumProjectState BUILDING = new ContinuumProjectState( "building" );

    private String name;

    protected ContinuumProjectState( String name )
    {
        this.name = name;
    }

    public String getI18nKey()
    {
        return "org.codehaus.continuum.project.state." + name;
    }

    public String toString()
    {
        return name;
    }
}
