package org.codehaus.continuum.project;

/*
 * LICENSE
 */

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AbstractContinuumProject.java,v 1.1 2004-07-07 02:34:35 trygvis Exp $
 */
public abstract class AbstractContinuumProject
    implements ContinuumProject
{
    /** */
    private String id;

    /** */
    private String type;

    /** */
    private String name;

    /** */
    private String scmConnection;

    /** */
    private int state;

    /** */
    private ProjectDescriptor descriptor;

    /** */
//    private List builds;

    /**
     * @return Returns the id.
     */
    public String getId()
    {
        return id;
    }

    /**
     * @param id The id to set.
     */
    public void setId( String id )
    {
        this.id = id;
    }

    /**
     * @return Returns the type.
     */
    public String getType()
    {
        return type;
    }

    /**
     * @param type The type to set.
     */
    public void setType( String type )
    {
        this.type = type;
    }

    /**
     * @return Returns the name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName( String name )
    {
        this.name = name;
    }

    /**
     * @return Returns the scm connection.
     */
    public String getScmConnection()
    {
        return scmConnection;
    }

    /**
     * @param ccmConnection The ccmConnection to set.
     */
    public void setScmConnection( String scmConnection)
    {
        this.scmConnection = scmConnection;
    }

    /**
     * @return Returns the state.
     */
    public int getState()
    {
        return state;
    }

    /**
     * @param state The state to set.
     */
    public void setState( int state )
    {
        this.state = state;
    }

    public ProjectDescriptor getDescriptor()
    {
        return descriptor;
    }

    public void setDescriptor( ProjectDescriptor descriptor )
    {
        this.descriptor = descriptor;
    }
/*
    public List getBuilds()
    {
        return builds;
    }

    protected void setBuilds( List builds )
    {
        this.builds = builds;
    }
*/
}
