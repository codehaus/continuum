package org.codehaus.continuum.cli;

/*
 * LICENSE
 */

import org.apache.commons.cli.ParseException;

import org.codehaus.continuum.ContinuumException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AddProjectCliCommand.java,v 1.1 2004-07-07 05:05:36 trygvis Exp $
 */
public class AddProjectCliCommand
    extends AbstractCliCommand
{
    public AddProjectCliCommand()
    {
        super( "addProject" );
    }

    public void execute( String[] args )
        throws ContinuumException, ParseException
    {
        addOption( projectNameOption );

        addOption( scmConnectionOption );

        addOption( projectTypeOption );

        initialize( args );

        String projectName = getProjectNameOption();

        String scmConnection = getScmConnectionOption();

        String type = getProjectTypeOption();

        getContinuum().addProject( projectName, scmConnection, type );
    }
}
