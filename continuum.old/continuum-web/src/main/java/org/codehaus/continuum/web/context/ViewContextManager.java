package org.codehaus.continuum.web.context;

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

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ViewContextManager.java,v 1.2 2004-10-06 14:24:25 trygvis Exp $
 */
public interface ViewContextManager
{
    String ROLE = ViewContextManager.class.getName();

    /** Map of scalars to place in the context. */
    Map getScalars( String view, Object model, Map parameters );
}
