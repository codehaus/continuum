package org.codehaus.continuum.web.action;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

import java.util.Map;

import org.codehaus.plexus.logging.AbstractLogEnabled;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultActionManager.java,v 1.1 2004-07-27 05:16:12 trygvis Exp $
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
