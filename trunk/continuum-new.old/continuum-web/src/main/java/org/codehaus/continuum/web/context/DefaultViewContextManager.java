package org.codehaus.continuum.web.context;

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

import ognl.Ognl;
import ognl.OgnlException;
import org.codehaus.plexus.logging.AbstractLogEnabled;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultViewContextManager.java,v 1.2 2005-03-10 00:05:57 trygvis Exp $
 */
public class DefaultViewContextManager
    extends AbstractLogEnabled
    implements ViewContextManager
{
    private Map views;

    public Map getScalars( String view, Object model, Map parameters )
    {
        Map scalars = (Map) views.get( view );

        Map contextScalars = new HashMap();

        for ( Iterator i = scalars.values().iterator(); i.hasNext(); )
        {
            Scalar scalar = (Scalar) i.next();

            try
            {
                Object value = Ognl.getValue( scalar.getExpression(), parameters, model );

                contextScalars.put( scalar.getId(), value );
            }
            catch ( OgnlException e )
            {
                getLogger().error( "Cannot find a value for the expression " + scalar.getExpression() + "in " + model );
            }
        }

        return contextScalars;
    }
}
