package org.codehaus.continuum.web.tool;

/*
 * Copyright 2004-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: CssTool.java,v 1.2 2005-03-10 00:05:59 trygvis Exp $
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
