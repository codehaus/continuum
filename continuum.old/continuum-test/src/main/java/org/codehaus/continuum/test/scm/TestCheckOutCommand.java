package org.codehaus.continuum.test.scm;

/*
 * LICENSE
 */

import org.apache.maven.scm.command.checkout.CheckOutCommand;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: TestCheckOutCommand.java,v 1.1 2004-07-19 16:54:47 trygvis Exp $
 */
public class TestCheckOutCommand
    extends AbstractTestCommand
{
    public TestCheckOutCommand()
    {
        super( CheckOutCommand.NAME, "Check out" );
    }

    public void execute()
        throws Exception
    {
        System.out.println( "Working directory: " + this.getWorkingDirectory() );
        System.out.println( "Connection: " + this.getRepository().getConnection() );
    }
}
