package org.codehaus.continuum.web.model;

/*
 * LICENSE
 */

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ProjectModel.java,v 1.1 2004-07-27 05:16:12 trygvis Exp $
 */
public class ProjectModel
{
    private String id;

    private String name;

    private String state;

    private String scmConnection;

    private String type;

    private ProjectDescriptorModel descriptor;

    /**
     * @param name
     * @param scmConnection
     * @param type
     * @param goals
     */
    public ProjectModel( String id, String name, String scmConnection, String state, String type, ProjectDescriptorModel descriptor )
    {
        this.id = id;

        this.name = name;

        this.scmConnection = scmConnection;

        this.state = state;

        this.type = type;

        this.descriptor = descriptor;
    }

    /**
     * @return Returns the id.
     */
    public String getId()
    {
        return id;
    }

    /**
     * @return Returns the name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return Returns the scmConnection.
     */
    public String getScmConnection()
    {
        return scmConnection;
    }

    /**
     * @return Returns the state.
     */
    public String getState()
    {
        return state;
    }

    /**
     * @return Returns the type.
     */
    public String getType()
    {
        return type;
    }

    /**
     * @return Returns the descriptor.
     */
    public ProjectDescriptorModel getDescriptor()
    {
        return descriptor;
    }
}
