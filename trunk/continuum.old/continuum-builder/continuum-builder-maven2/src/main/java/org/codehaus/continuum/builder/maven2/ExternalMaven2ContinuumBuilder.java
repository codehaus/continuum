package org.codehaus.continuum.builder.maven2;

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

import org.apache.maven.project.MavenProject;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.builder.ContinuumBuilder;
import org.codehaus.continuum.project.ContinuumBuild;
import org.codehaus.continuum.project.ContinuumBuildResult;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.CommandLineUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ExternalMaven2ContinuumBuilder.java,v 1.3 2004-10-14 14:29:57 trygvis Exp $
 */
public class ExternalMaven2ContinuumBuilder
    extends Maven2ContinuumBuilder
    implements ContinuumBuilder
{
    // ----------------------------------------------------------------------
    // ContinuumBuilder implementation
    // ----------------------------------------------------------------------

    public synchronized ContinuumBuildResult build( File workingDirectory, ContinuumBuild build )
        throws ContinuumException
    {
        Maven2ProjectDescriptor descriptor;

        descriptor = (Maven2ProjectDescriptor) build.getProject().getDescriptor();

        List goals = descriptor.getGoals();

        // ----------------------------------------------------------------------
        // Build it
        // ----------------------------------------------------------------------

        return execute( workingDirectory, descriptor.getMavenProject(), build, goals );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    protected Maven2BuildResult execute( File workingDirectory, MavenProject mavenProject, ContinuumBuild build, List goals )
        throws ContinuumException
    {
        Commandline cl = getMaven2CommandLine( workingDirectory, goals );

        CommandLineUtils.StringStreamConsumer stderr = new CommandLineUtils.StringStreamConsumer();

        CommandLineUtils.StringStreamConsumer stdout = new CommandLineUtils.StringStreamConsumer();

        int exitCode;

        try
        {
            exitCode = CommandLineUtils.executeCommandLine( cl, stdout, stderr );
        }
        catch( Exception ex )
        {
            throw new ContinuumException( "Error while executing command.", ex );
        }

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        String out = stdout.getOutput();

        String err = stderr.getOutput();

        boolean success = out.indexOf( "BUILD SUCCESSFUL" ) != -1;

        if ( build != null )
        {
            ExternalMaven2BuildResult result = new ExternalMaven2BuildResult( build, success, out, err, exitCode );

            return result;
        }
        else
        {
            return null;
        }
    }

    private Commandline getMaven2CommandLine( File workingDirectory, List goals )
    {
        Commandline cl = new Commandline();

        cl.setExecutable( "java" );

        cl.setWorkingDirectory( workingDirectory.getAbsolutePath() );

        File classWorldsJar = new File( getMavenHome(), "/core/classworlds-1.1-SNAPSHOT.jar" );

        cl.createArgument().setValue( "-classpath" );

        cl.createArgument().setValue( classWorldsJar.getAbsolutePath() );

        cl.createArgument().setValue( "-Dclassworlds.conf=" + getMavenHome() + "/bin/classworlds.conf" );

        if ( !StringUtils.isEmpty( getMavenRepository() ) )
        {
            cl.createArgument().setValue( "-Dmaven.repo.local=" + getMavenRepository() );
        }

        cl.createArgument().setValue( "org.codehaus.classworlds.Launcher" );

        for ( Iterator it = goals.iterator(); it.hasNext(); )
        {
            String goal = (String) it.next();

            cl.createArgument().setValue( goal );
        }

        String[] args = cl.getCommandline();

        String cmd = args[0];

        for ( int i = 1; i < args.length; i++ )
        {
            cmd += " " + args[i];
        }

        getLogger().warn( "Executing external maven. Commandline: " + cmd );

        getLogger().warn( "Executing external maven. Working directory: " + cl.getWorkingDirectory().getAbsolutePath() );

        return cl;
    }
}
