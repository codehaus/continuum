package org.codehaus.continuum.cli;

/*
 * LICENSE
 */

import org.apache.commons.cli.ParseException;

import org.codehaus.continuum.ContinuumException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: CliCommand.java,v 1.1 2004-07-07 05:05:36 trygvis Exp $
 */
public interface CliCommand
{
    String ROLE = CliCommand.class.getName();

    void execute( String[] args )
        throws ContinuumException, ParseException;

    String getCommandLineHelp();
}
