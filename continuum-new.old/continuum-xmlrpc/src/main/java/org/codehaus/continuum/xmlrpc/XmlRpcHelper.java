package org.codehaus.continuum.xmlrpc;

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

import java.util.Hashtable;
import java.lang.reflect.InvocationTargetException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: XmlRpcHelper.java,v 1.1.1.1 2005-03-20 22:59:13 trygvis Exp $
 */
public interface XmlRpcHelper
{
    String ROLE = XmlRpcHelper.class.getName();

    Hashtable objectToHashtable( Object object )
        throws IllegalAccessException, InvocationTargetException;
}
