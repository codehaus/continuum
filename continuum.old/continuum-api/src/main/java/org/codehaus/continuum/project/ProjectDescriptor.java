package org.codehaus.continuum.project;

/*
 * LICENSE
 */

import java.io.Serializable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ProjectDescriptor.java,v 1.1 2004-07-20 18:25:57 trygvis Exp $
 */
public interface ProjectDescriptor
    extends Serializable
{
    /**
     * @return Returns the project.
     */
    ContinuumProject getProject();

    /**
     * @param project The project to set.
     */
    void setProject( ContinuumProject project );

    String getProjectId();

    void setProjectId( String projectId );

//    String getType();

//    void setType( String type );
}
