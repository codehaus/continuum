package org.codehaus.continuum.cli;

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

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;
import org.apache.commons.cli.PosixParser;

import org.codehaus.continuum.Continuum;
import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.xmlrpc.XmlRpcContinuumClient;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AbstractCliCommand.java,v 1.1 2004-10-09 11:21:27 trygvis Exp $
 */
public abstract class AbstractCliCommand
    implements CliCommand
{
    private final static String HOSTNAME = "h";

    private String hostname;

    protected final static Option hostnameOption = OptionBuilder
        .withDescription( "The hostname to connect to." )
        .withLongOpt( "hostname" )
        .withArgName( "hostname" )
        .hasArg()
        .create( HOSTNAME );

    /** */
    private String port;

    private final static String PORT = "p";

    protected final static Option portOption = OptionBuilder
        .withDescription( "The port to connect to." )
        .withLongOpt( "port" )
        .withArgName( "port" )
        .hasArg()
        .create( PORT );

    /** */
    private String projectName;

    private final static String PROJECT_NAME = "n";

    protected final static Option projectNameOption = OptionBuilder
        .withDescription( "The name of the project." )
        .withLongOpt( "project-name" )
        .withArgName( "project name" )
        .hasArg()
        .create( PROJECT_NAME );

    /** */
    private String scmConnection;

    private final static String SCM_CONNECTION = "s";

    protected final static Option scmConnectionOption = OptionBuilder
        .hasArg()
        .withDescription( "The scm connection string to the project." )
        .withLongOpt( "scm-connection" )
        .withArgName( "scm connection" )
        .create( SCM_CONNECTION );

    /** */
    private String projectType;

    private final static String PROJECT_TYPE = "t";

    protected final static Option projectTypeOption = OptionBuilder
        .hasArg()
        .withDescription( "The type of project." )
        .withLongOpt( "project-type" )
        .withArgName( "project type" )
        .create( PROJECT_TYPE );

    private Options options = null;

    /** */
    private XmlRpcContinuumClient continuum;

    /** */
    private String commandName;

    /** */
    private CommandLine commandLine;

    public AbstractCliCommand( String commandName )
    {
        this.commandName = commandName;

        options = new Options();

        options.addOption( hostnameOption );

        options.addOption( portOption );
    }

    protected Continuum getContinuum()
    {
        return continuum;
    }

    protected void initialize( String[] args )
        throws ParseException
    {
        Parser parser = new PosixParser();

        commandLine = parser.parse( options, args );
    }

    public String getCommandLineHelp()
    {
        HelpFormatter formatter = new HelpFormatter();

        StringWriter string = new StringWriter();

        PrintWriter output = new PrintWriter( string );

        formatter.printHelp( output, 100, commandName, null, options, 4, 10, null );

        return string.toString();
    }

    protected void addOption( Option option )
    {
        options.addOption( option );
    }

    protected String getHostnameOption()
        throws ContinuumException
    {
        return getOption( HOSTNAME );
    }

    protected String getPortOption()
        throws ContinuumException
    {
        return getOption( PORT );
    }

    protected String getProjectNameOption()
        throws ContinuumException
    {
        return getOption( PROJECT_NAME );
    }

    protected String getScmConnectionOption()
        throws ContinuumException
    {
        return getOption( SCM_CONNECTION );
    }

    protected String getProjectTypeOption()
        throws ContinuumException
    {
        return getOption( PROJECT_TYPE );
    }

    private String getOption( String key )
        throws ContinuumException
    {
        Option option = options.getOption( key );

        if ( option == null )
        {
            throw new ContinuumException( "No such option: '" + key + "'." );
        }

        String value = commandLine.getOptionValue( key );

        return value;
    }
}
