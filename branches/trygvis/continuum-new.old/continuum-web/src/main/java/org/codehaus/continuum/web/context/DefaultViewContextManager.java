package org.codehaus.continuum.web.context;

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

import ognl.Ognl;
import ognl.OgnlException;
import org.codehaus.plexus.logging.AbstractLogEnabled;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultViewContextManager.java,v 1.1.1.1 2005-02-17 22:23:57 trygvis Exp $
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
