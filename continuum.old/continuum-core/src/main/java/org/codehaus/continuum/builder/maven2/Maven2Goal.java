package org.codehaus.continuum.builder.maven2;

/*
 * LICENSE
 */

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: Maven2Goal.java,v 1.1 2004-07-08 01:13:35 trygvis Exp $
 */
public class Maven2Goal
{
    private String projectId;
    private int index;
    private String name;

    /**
     * @return Returns the index.
     */
    public int getIndex()
    {
        return index;
    }

    /**
     * @param index The index to set.
     */
    public void setIndex( int index )
    {
        this.index = index;
    }

    /**
     * @return Returns the projectId.
     */
    public String getProjectId()
    {
        return projectId;
    }

    /**
     * @param projectId The projectId to set.
     */
    public void setProjectId( String projectId )
    {
        this.projectId = projectId;
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
}
