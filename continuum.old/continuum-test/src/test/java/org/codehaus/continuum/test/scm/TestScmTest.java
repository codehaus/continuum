package org.codehaus.continuum.test.scm;

/*
 * LICENSE
 */

import java.io.File;

import org.apache.maven.scm.command.Command;
import org.apache.maven.scm.command.checkout.CheckOutCommand;
import org.apache.maven.scm.manager.ScmManager;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.FileUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: TestScmTest.java,v 1.1 2004-07-27 00:06:12 trygvis Exp $
 */
public class TestScmTest
    extends PlexusTestCase
{
    public void testDummy()
        throws Exception
    {
        ScmManager scmManager = (ScmManager) lookup( ScmManager.ROLE );

        String scmUrl = "scm:test:";

        scmManager.setRepositoryInfo( scmUrl );

        Command checkOut = scmManager.getCommand( CheckOutCommand.NAME );

        TestRepository repository = (TestRepository) checkOut.getRepository();

        assertFalse( repository.isValid() );
    }

    public void testTestScm()
        throws Exception
    {
        ScmManager scmManager = (ScmManager) lookup( ScmManager.ROLE );

        String scmUrl = "scm:test:" + getTestFile( "src/test/repositories") + ":test-repo";

        scmManager.setRepositoryInfo( scmUrl );

        Command checkOut = scmManager.getCommand( CheckOutCommand.NAME );

        String workingDirectory = getTestFile( "target/test-scm-test" );

        FileUtils.deleteDirectory( workingDirectory );

        FileUtils.mkdir( workingDirectory );

        checkOut.setWorkingDirectory( workingDirectory );

        checkOut.execute();

        File coDir = new File( workingDirectory, "test-repo" );

        assertTrue( coDir.exists() );

        assertTrue( coDir.isDirectory() );

        File testFile = new File( coDir, "Test.java" );

        assertTrue( testFile.exists() );

        assertTrue( testFile.isFile() );
    }
}
