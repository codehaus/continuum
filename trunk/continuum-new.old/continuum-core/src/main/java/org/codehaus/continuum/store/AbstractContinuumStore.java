package org.codehaus.continuum.store;

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

import java.io.StringWriter;
import java.io.PrintWriter;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.project.ContinuumBuild;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AbstractContinuumStore.java,v 1.4 2005-03-10 00:05:53 trygvis Exp $
 */
public abstract class AbstractContinuumStore
    extends AbstractLogEnabled
    implements ContinuumStore
{
    // TODO: Remove
    protected ContinuumProject getGenericProject( String id )
        throws ContinuumStoreException
    {
        return getProject( id );
    }

    // TODO: Remove
    protected ContinuumBuild getGenericBuild( String id )
        throws ContinuumStoreException
    {
        return getBuild( id );
    }

    public static String throwableToString( Throwable error )
    {
        if ( error == null )
        {
            return "";
        }

        StringWriter writer = new StringWriter();

        PrintWriter printer = new PrintWriter( writer );

        error.printStackTrace( printer );

        printer.flush();

        return writer.getBuffer().toString();
    }
 }
