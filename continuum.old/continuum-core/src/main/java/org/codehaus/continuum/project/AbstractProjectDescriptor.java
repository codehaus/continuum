package org.codehaus.continuum.project;

/*
 * LICENSE
 */

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AbstractProjectDescriptor.java,v 1.1 2004-07-07 02:34:35 trygvis Exp $
 */
public abstract class AbstractProjectDescriptor
    implements ProjectDescriptor
{
    /** */
    private ContinuumProject project;

    /** */
    private String projectId;

    /** */
    private String type;

    /**
     * @return Returns the project.
     */
    public ContinuumProject getProject()
    {
        return project;
    }

    /**
     * @param project The project to set.
     */
    public void setProject( ContinuumProject project )
    {
        this.project = project;
    }

    public String getProjectId()
    {
        return projectId;
    }

    public void setProjectId( String projectId )
    {
        this.projectId = projectId;
    }

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }
}
