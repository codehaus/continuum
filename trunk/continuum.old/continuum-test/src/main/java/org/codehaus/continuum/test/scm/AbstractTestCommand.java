package org.codehaus.continuum.test.scm;

/*
 * LICENSE
 */

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.command.Command;
import org.apache.maven.scm.repository.Repository;

import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l </a>
 * @version $Id: AbstractTestCommand.java,v 1.1 2004-07-19 16:54:47 trygvis Exp $
 */
public abstract class AbstractTestCommand
    implements Command
{
    private String name;

    private String displayName;

    private Repository repository;

    private String workingDir;

    private String branchName;

    private String tagName;

    private StreamConsumer streamConsumer;

    public AbstractTestCommand( String name, String displayName )
    {
        this.name = name;
        this.displayName = displayName;
    }

    public String getName()
    {
        return name;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setRepository( Repository repository )
        throws ScmException
    {
        this.repository = repository;
    }

    public Repository getRepository()
    {
        return repository;
    }

    public void setWorkingDirectory( String workingDir )
    {
        this.workingDir = workingDir;
    }

    public String getWorkingDirectory()
    {
        return workingDir;
    }

    public void setBranch( String branchName )
    {
        this.branchName = branchName;
    }

    public String getBranch()
    {
        return branchName;
    }

    public void setTag( String tagName )
    {
        this.tagName = tagName;
    }

    public String getTag()
    {
        return tagName;
    }

    public void setConsumer( StreamConsumer consumer )
        throws ScmException
    {
        this.streamConsumer = consumer;
    }

    public StreamConsumer getConsumer()
    {
        return streamConsumer;
    }

    public Commandline getCommandLine()
        throws ScmException
    {
        return null;
    }
}