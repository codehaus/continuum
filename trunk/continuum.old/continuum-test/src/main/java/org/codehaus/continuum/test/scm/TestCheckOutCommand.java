package org.codehaus.continuum.test.scm;

/*
 * LICENSE
 */

import java.io.File;

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.command.checkout.CheckOutCommand;

import org.codehaus.plexus.util.FileUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: TestCheckOutCommand.java,v 1.2 2004-07-27 00:06:11 trygvis Exp $
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

        FileUtils.copyDirectory( source, destination );
    }
}
