package org.codehaus.continuum.registration;

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

import org.codehaus.continuum.Continuum;
import org.codehaus.continuum.utils.PlexusUtils;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: AbstractContinuumRegistrar.java,v 1.2 2005-03-10 00:05:52 trygvis Exp $
 */
public abstract class AbstractContinuumRegistrar
    extends AbstractLogEnabled
    implements ContinuumRegistrar, Initializable
{
    private Continuum continuum;

    public void initialize()
        throws Exception
    {
        PlexusUtils.assertRequirement( continuum, Continuum.ROLE );
    }

    public Continuum getContinuum()
    {
        return continuum;
    }
}
