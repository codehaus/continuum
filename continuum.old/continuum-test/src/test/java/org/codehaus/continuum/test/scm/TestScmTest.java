package org.codehaus.continuum.test.scm;

/*
 * Copyright (c) 2004, Jason van Zyl and Trygve Laugstøl
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.io.File;

import org.apache.maven.scm.command.Command;
import org.apache.maven.scm.command.checkout.CheckOutCommand;
import org.apache.maven.scm.manager.ScmManager;

import org.codehaus.plexus.ArtifactEnabledPlexusTestCase;
import org.codehaus.plexus.util.FileUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: TestScmTest.java,v 1.5 2004-10-06 13:33:51 trygvis Exp $
 */
public class TestScmTest
    extends ArtifactEnabledPlexusTestCase
{
    public void testInvalidRepository()
        throws Exception
    {
        ScmManager scmManager = (ScmManager) lookup( ScmManager.ROLE );

        String scmUrl = "scm:test:";

        scmManager.setRepositoryInfo( scmUrl );

        try
        {
            scmManager.getCommand( CheckOutCommand.NAME );

            fail( "Expected InvalidConnectionUrlException." );
        }
        catch( InvalidConnectionUrlException ex )
        {
            // expected
        }
    }

    public void testTestScmWithRelativePath()
        throws Exception
    {
        coTest( "scm:test:src/test/repositories:test-repo" );
    }

    public void testTestScmWithAbsolutePath()
        throws Exception
    {
        coTest( "scm:test:" + getTestPath( "src/test/repositories" ) + ":test-repo" );
    }

    private void coTest( String scmUrl )
        throws Exception
    {
        ScmManager scmManager = (ScmManager) lookup( ScmManager.ROLE );

        scmManager.setRepositoryInfo( scmUrl );

        Command checkOut = scmManager.getCommand( CheckOutCommand.NAME );

        File workingDirectory = getTestFile( "target/test-scm-test" );

        FileUtils.deleteDirectory( workingDirectory );

        assertTrue( workingDirectory.mkdir() );

        checkOut.setWorkingDirectory( workingDirectory.getAbsolutePath() );

        checkOut.execute();

        File coDir = new File( workingDirectory, "test-repo" );

        assertTrue( coDir.isDirectory() );

        File testFile = new File( coDir, "Test.java" );

        assertTrue( testFile.isFile() );
    }
}
