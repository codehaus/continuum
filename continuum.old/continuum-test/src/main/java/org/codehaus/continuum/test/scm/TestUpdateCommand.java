package org.codehaus.continuum.test.scm;

/*
 * LICENSE
 */

import org.apache.maven.scm.command.update.UpdateCommand;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: TestUpdateCommand.java,v 1.1 2004-07-19 16:54:47 trygvis Exp $
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
        System.out.println( "Working directory: " + this.getWorkingDirectory() );
        System.out.println( "Connection: " + this.getRepository().getConnection() );
    }
}
