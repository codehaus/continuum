package org.codehaus.continuum.project;

/*
 * LICENSE
 */

import java.io.Serializable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumProject.java,v 1.1 2004-07-20 18:25:57 trygvis Exp $
 */
public interface ContinuumProject
    extends Serializable
{
    /**
     * @return Returns the id.
     */
    public String getId();

    /**
     * @param id The id to set.
     */
    public void setId( String id );

    /**
     * @return Returns the type.
     */
    public String getType();

    /**
     * @param type The type to set.
     */
    public void setType( String type );

    /**
     * @return Returns the name.
     */
    public String getName();

    /**
     * @param name The name to set.
     */
    public void setName( String name );

    /**
     * @return Returns the scm connection.
     */
    public String getScmConnection();

    /**
     * @param ccmConnection The ccmConnection to set.
     */
    public void setScmConnection( String scmConnection );

    /**
     * @return Returns the state.
     */
    public ContinuumProjectState getState();

    /**
     * @param state The state to set.
     */
    public void setState( ContinuumProjectState state );

    public ProjectDescriptor getDescriptor();

    public void setDescriptor( ProjectDescriptor descriptor );

//    public List getBuilds();
}
