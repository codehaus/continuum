package org.codehaus.continuum.builder.test;

/*
 * LICENSE
 */

import org.codehaus.continuum.project.AbstractContinuumBuildResult;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: TestBuildResult.java,v 1.1 2004-07-27 00:06:10 trygvis Exp $
 */
public class TestBuildResult
    extends AbstractContinuumBuildResult
{
    private boolean success;

    public TestBuildResult( boolean success )
    {
        this.success = success;
    }

    public boolean isSuccess()
    {
        return success;
    }
}
