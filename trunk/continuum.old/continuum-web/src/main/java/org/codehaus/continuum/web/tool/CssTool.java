package org.codehaus.continuum.web.tool;

/*
 * LICENSE
 */

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: CssTool.java,v 1.1 2004-10-30 13:12:48 trygvis Exp $
 */
public class CssTool
{
    private ThreadLocal state = new ThreadLocal();

    private static class State
    {
        public List classes = new ArrayList();

        public int currentClass;
    }

    public String getNextClass()
    {
        State state = getState();

        if ( state.classes.size() == 0 )
        {
            throw new RuntimeException( "The CssTool haven't been reset() by the template." );
        }

        if ( state.currentClass == state.classes.size() )
        {
            state.currentClass = 0;
        }

        return state.classes.get( state.currentClass++ ).toString();
    }

    public void reset()
    {
        State state = getState();

        state.classes = new ArrayList();

        state.classes.add( "a" );

        state.classes.add( "b" );

        state.currentClass = 0;
    }

    // ----------------------------------------------------------------------
    // 
    // ----------------------------------------------------------------------

    private State getState()
    {
        State state = (State) this.state.get();

        if ( state == null )
        {
            state = new CssTool.State();
            this.state.set( state );
        }

        return state;
    }
}
