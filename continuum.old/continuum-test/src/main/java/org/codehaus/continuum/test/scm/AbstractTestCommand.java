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

import org.apache.maven.scm.command.Command;
import org.apache.maven.scm.repository.Repository;

import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l </a>
 * @version $Id: AbstractTestCommand.java,v 1.3 2004-07-29 04:27:42 trygvis Exp $
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
    {
        this.streamConsumer = consumer;
    }

    public StreamConsumer getConsumer()
    {
        return streamConsumer;
    }

    public Commandline getCommandLine()
    {
        return null;
    }
}
