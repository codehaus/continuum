package org.codehaus.continuum.project;

/*
 * LICENSE
 */

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: GenericBuildResult.java,v 1.1 2004-07-07 02:34:35 trygvis Exp $
 */
public class GenericBuildResult
    extends AbstractBuildResult
{
    public GenericBuildResult()
    {
    }

    public GenericBuildResult( String buildId )
    {
        setBuildId( buildId );
    }
}
