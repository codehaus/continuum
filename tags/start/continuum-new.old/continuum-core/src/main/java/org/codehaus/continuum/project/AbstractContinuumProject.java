package org.codehaus.continuum.project;

/*
 * The MIT License
 *
 * Copyright (c) 2004, Jason van Zyl and Trygve Laugst�l
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

import java.util.Properties;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AbstractContinuumProject.java,v 1.1.1.1 2005-02-17 22:23:52 trygvis Exp $
 */
public abstract class AbstractContinuumProject
    implements ContinuumProject
{
    private String id;

    private String builderId;

    private String name;

    private String scmUrl;

    private String nagEmailAddress;

    private String version;

    private String workingDirectory;

    private ContinuumProjectState state;

    private Properties configuration;

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public String getBuilderId()
    {
        return builderId;
    }

    public void setBuilderId( String type )
    {
        this.builderId = type;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getScmUrl()
    {
        return scmUrl;
    }

    public void setScmUrl( String scmUrl )
    {
        this.scmUrl = scmUrl;
    }

    public String getNagEmailAddress()
    {
        return nagEmailAddress;
    }

    public void setNagEmailAddress( String nagEmailAddress )
    {
        this.nagEmailAddress = nagEmailAddress;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion( String version )
    {
        this.version = version;
    }

    public String getWorkingDirectory()
    {
        return workingDirectory;
    }

    public void setWorkingDirectory( String workingDirectory )
    {
        this.workingDirectory = workingDirectory;
    }

    public ContinuumProjectState getState()
    {
        return state;
    }

    public void setState( ContinuumProjectState state )
    {
        this.state = state;
    }

    public Properties getConfiguration()
    {
        return configuration;
    }

    public void setConfiguration( Properties configuration )
    {
        this.configuration = configuration;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        sb.append( "id = " ).append( id ).append( "\n" );

        sb.append( "builderId = " ).append( builderId ).append( "\n" );

        sb.append( "name = " ).append( name ).append( "\n" );

        sb.append( "scmUrl = " ).append( scmUrl ).append( "\n" );

        sb.append( "nagEmail = " ).append( nagEmailAddress ).append( "\n" );

        sb.append( "version = " ).append( version ).append( "\n" );

        sb.append( "workingDirectory = " ).append( workingDirectory ).append( "\n" );
        
        return sb.toString();
    }

}
