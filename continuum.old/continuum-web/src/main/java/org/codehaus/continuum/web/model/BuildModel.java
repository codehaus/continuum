package org.codehaus.continuum.web.model;

/*
 * LICENSE
 */

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: BuildModel.java,v 1.1 2004-07-27 05:16:12 trygvis Exp $
 */
public class BuildModel
{
    private String startTime;

    private String endTime;

    private String state;

    /**
     * @param startTime
     * @param endTime
     * @param state
     */
    public BuildModel( String startTime, String endTime, String state )
    {
        this.startTime = startTime;
        this.endTime = endTime;
        this.state = state;
    }

    /**
     * @return Returns the startTime.
     */
    public String getStartTime()
    {
        return startTime;
    }

    /**
     * @param startTime The startTime to set.
     */
    public void setStartTime( String startTime )
    {
        this.startTime = startTime;
    }

    /**
     * @return Returns the endTime.
     */
    public String getEndTime()
    {
        return endTime;
    }

    /**
     * @param endTime The endTime to set.
     */
    public void setEndTime( String endTime )
    {
        this.endTime = endTime;
    }

    /**
     * @return Returns the state.
     */
    public String getState()
    {
        return state;
    }

    /**
     * @param state The state to set.
     */
    public void setState( String state )
    {
        this.state = state;
    }
}
