package org.codehaus.continuum.test.scm;

/*
 * LICENSE
 */

import org.apache.maven.scm.command.update.UpdateCommand;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: TestUpdateCommand.java,v 1.2 2004-07-27 00:06:11 trygvis Exp $
 */
public class TestUpdateCommand
    extends AbstractTestCommand
{
    public TestUpdateCommand()
    {
        super( UpdateCommand.NAME, "Update" );
    }

    public void execute()
        throws Exception
    {
        throw new UnsupportedOperationException();
    }
}
