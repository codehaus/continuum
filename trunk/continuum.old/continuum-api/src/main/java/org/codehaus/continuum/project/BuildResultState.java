package org.codehaus.continuum.project;

/*
 * LICENSE
 */

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: BuildResultState.java,v 1.1 2004-07-20 18:25:57 trygvis Exp $
 */
public class BuildResultState
{
    /** */
    public final static BuildResultState BUILDING = new BuildResultState( "building" );

    /** */
    public final static BuildResultState RESULT_OK = new BuildResultState( "ok" );

    /** */
    public final static BuildResultState RESULT_FAILURE = new BuildResultState( "failure" );

    /** */
    public final static BuildResultState RESULT_ERROR = new BuildResultState( "error" );

    private final String name;

    private BuildResultState( String name )
    {
        this.name = name;
    }

    public String getI18nKey()
    {
        return "org.codehaus.continuum.project.BuildResult.state." + name;
    }
}
