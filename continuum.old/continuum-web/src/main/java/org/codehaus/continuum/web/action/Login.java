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

import org.codehaus.plexus.summit.rundata.RunData;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: Login.java,v 1.1 2004-07-27 05:16:12 trygvis Exp $
 */
public class Login
    extends AbstractAction
{
    public void execute( Map map )
        throws Exception
    {
        String login = (String) map.get( "login.username" );

        getLogger().info( "Trying to log in " + login );

        String password = (String) map.get( "login.password" );

        if ( login.equals( "admin" ) && password.equals( "admin" ) )
        {
            RunData data = (RunData) map.get( "data" );

            data.getRequest().getSession().setAttribute( "loggedIn", "true" );
        }
    }
}
