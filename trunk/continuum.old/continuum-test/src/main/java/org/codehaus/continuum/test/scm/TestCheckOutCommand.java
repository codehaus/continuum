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
import java.util.Iterator;
import java.util.List;

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.command.checkout.CheckOutCommand;

import org.codehaus.plexus.util.FileUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: TestCheckOutCommand.java,v 1.5 2004-10-06 13:33:50 trygvis Exp $
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

        File base = new File( repository.getBase() );

        String dir = repository.getDir();

        File source = new File( base, dir );

        File baseDestination = new File( workingDirectory, dir );

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

        FileUtils.deleteDirectory( baseDestination );

        if ( !baseDestination.mkdirs() )
        {
            throw new ScmException( "Could not create destination directory '" + baseDestination.getAbsolutePath() + "'." );
        }

        System.err.println( "Checking out '" + source.getAbsolutePath() + "' to '" + baseDestination.getAbsolutePath() + "'." );

        List files = FileUtils.getFiles( source.getAbsoluteFile(), "**", null );

        String sourcePath = source.getAbsolutePath();

        for ( Iterator i = files.iterator(); i.hasNext(); )
        {
            File file = (File) i.next();

            String dest = file.getAbsolutePath();

            dest = dest.substring( sourcePath.length() + 1 );

            File destination = new File( baseDestination, dest );

            destination = destination.getParentFile();

            if ( !destination.exists() && !destination.mkdirs() )
            {
                throw new ScmException( "Could not create destination directory '" + destination.getAbsolutePath() + "'." );
            }

            FileUtils.copyFileToDirectory( file, destination );
        }
    }
}
