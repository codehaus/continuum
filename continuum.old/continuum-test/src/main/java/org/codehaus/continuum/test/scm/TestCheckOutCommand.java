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

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.command.checkout.CheckOutCommand;

import org.codehaus.plexus.util.FileUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: TestCheckOutCommand.java,v 1.3 2004-07-29 04:27:42 trygvis Exp $
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
        File workingDirectory = new File( getWorkingDirectory() );

        TestRepository repository = (TestRepository) getRepository();

        if ( !repository.isValid() )
        {
            return;
        }

        File base = new File( repository.getBase() );

        String dir = repository.getDir();

        File source = new File( base, dir );

        File destination = new File( workingDirectory, dir );

        if ( !workingDirectory.exists() )
        {
            throw new ScmException( "The working directory doesn't exist (" + workingDirectory.getAbsolutePath() + ")." );
        }

        if ( !base.exists() )
        {
            throw new ScmException( "The base directory doesn't exist (" + base.getAbsolutePath() + ")." );
        }

        if ( !source.exists() )
        {
            throw new ScmException( "The module directory doesn't exist (" + source.getAbsolutePath() + ")." );
        }

        if ( !destination.mkdirs() )
        {
            throw new ScmException( "Could not create destination directory: " + destination.getAbsolutePath() );
        }

        System.out.println( "Checking out " + source.getAbsolutePath() + " to " + destination.getAbsolutePath() );

        FileUtils.copyDirectory( source, destination );
    }
}
