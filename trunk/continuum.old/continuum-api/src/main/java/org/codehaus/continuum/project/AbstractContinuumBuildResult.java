package org.codehaus.continuum.project;

/*
 * LICENSE
 */

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AbstractContinuumBuildResult.java,v 1.1 2004-07-27 00:06:03 trygvis Exp $
 */
public abstract class AbstractContinuumBuildResult
    implements ContinuumBuildResult
{
    private ContinuumBuild build;

    private String buildId;

    public AbstractContinuumBuildResult()
    {
    }

    public AbstractContinuumBuildResult( ContinuumBuild build )
    {
        this.build = build;

        this.buildId = build.getId();
    }

    public ContinuumBuild getBuild()
    {
        return build;
    }

    protected void setBuild( ContinuumBuild build )
    {
        this.build = build;
    }

    /**
     * @return Returns the buildId.
     */
    public String getBuildId()
    {
        return buildId;
    }

    /**
     * @param buildId The buildId to set.
     */
    private void setBuildId( String buildId )
    {
        this.buildId = buildId;
    }
}
