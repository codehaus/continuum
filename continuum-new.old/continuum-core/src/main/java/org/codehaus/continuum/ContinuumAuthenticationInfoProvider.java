package org.codehaus.continuum;

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

import org.apache.maven.artifact.repository.authentication.AuthenticationInfoProvider;
import org.apache.maven.wagon.repository.Repository;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumAuthenticationInfoProvider.java,v 1.1 2005-03-15 07:37:29 trygvis Exp $
 */
public class ContinuumAuthenticationInfoProvider
    extends AbstractLogEnabled
    implements AuthenticationInfoProvider, Initializable
{
    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
    {
    }

    // ----------------------------------------------------------------------
    // ContinuumAuthenticationInfoProvider Implementation
    // ----------------------------------------------------------------------

    public void configureAuthenticationInfo( Repository repository )
        throws Exception
    {
    }
}
