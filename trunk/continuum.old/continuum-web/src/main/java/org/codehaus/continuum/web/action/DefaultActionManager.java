package org.codehaus.continuum.web.action;

/*
 * Copyright (c) 2004, Jason van Zyl and Trygve Laugstøl
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

import java.util.Map;

import org.codehaus.plexus.logging.AbstractLogEnabled;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultActionManager.java,v 1.2 2004-07-29 04:38:09 trygvis Exp $
 */
public class DefaultActionManager
    extends AbstractLogEnabled
    implements ActionManager
{
    private Map actions;

    public Action lookupAction( String actionId )
        throws ActionNotFoundException
    {
//        getLogger().debug( "actions = " + actions );

        Action action = (Action) actions.get( actionId );

        if ( action == null )
        {
            throw new ActionNotFoundException( "Cannot find action: " + actionId );
        }

        return action;
    }

    /**
     * This is the action we use to adapt workflow actions into our normal actions.
     * We simply take the case attributes and place them into the ephemeral Map
     * otherAttributes for the execution of the action.
     *
     * @param caseAttributes Case process attributes.
     * @param otherAttributes Non case process attributes.
     * @throws Exception
     *//*
    public void executeWorkflowAction( Map caseAttributes, Map otherAttributes )
        throws Exception
    {
        String actionId = (String) otherAttributes.get( "actionId" );

        Action action = lookupAction( actionId );

        otherAttributes.put( AbstractWorkflowAction.CASE_ATTRIBUTES, caseAttributes );

        action.execute( otherAttributes );
    }
*/
    public void executeAction( String actionId, Map actionContext )
        throws Exception
    {
        lookupAction( actionId ).execute( actionContext );
    }
}
