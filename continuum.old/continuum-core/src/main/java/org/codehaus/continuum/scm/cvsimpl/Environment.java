/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000,2002-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "Ant" and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.codehaus.plexus.continuum.scm.cvsimpl;

import java.io.File;
import java.util.Vector;

/**
 * Wrapper for environment variables.
 *
 * @author Stefan Bodewig
 */
public class Environment
{

    /**
     * a vector of type Enviromment.Variable
     * @see Variable
     */
    protected Vector variables;

    /**
     * representation of a single env value
     */
    public static class Variable
    {

        /**
         * env key and value pair; everything gets expanded to a string
         * during assignment
         */
        private String key, value;

        /**
         * Constructor for variable
         *
         */
        public Variable()
        {
            super();
        }

        /**
         * set the key
         * @param key string
         */
        public void setKey( String key )
        {
            this.key = key;
        }

        /**
         * set the value
         * @param value string value
         */
        public void setValue( String value )
        {
            this.value = value;
        }

        /**
         * key accessor
         * @return key
         */
        public String getKey()
        {
            return this.key;
        }

        /**
         * value accessor
         * @return value
         */
        public String getValue()
        {
            return this.value;
        }

        /**
         * stringify path and assign to the value.
         * The value will contain all path elements separated by the appropriate
         * separator
         * @param path path
         */
        public void setPath( String path )
        {
            this.value = path;
        }

        /**
         * get the absolute path of a file and assign it to the value
         * @param file file to use as the value
         */
        public void setFile( File file )
        {
            this.value = file.getAbsolutePath();
        }

        /**
         * get the assigment string
         * This is not ready for insertion into a property file without following
         * the escaping rules of the properties class.
         * @return a string of the form key=value.
         */
        public String getContent() throws Exception
        {
            if ( key == null || value == null )
            {
                throw new Exception( "key and value must be specified "
                                          + "for environment variables." );
            }
            StringBuffer sb = new StringBuffer( key.trim() );
            sb.append( "=" ).append( value.trim() );
            return sb.toString();
        }
    }

    /**
     * constructor
     */
    public Environment()
    {
        variables = new Vector();
    }

    /**
     * add a variable.
     * Validity checking is <i>not</i> performed at this point. Duplicates
     * are not caught either.
     * @param var new variable.
     */
    public void addVariable( Variable var )
    {
        variables.addElement( var );
    }

    /**
     * get the variable list as an array
     * @return array of key=value assignment strings
     */
    public String[] getVariables()
        throws Exception
    {
        if ( variables.size() == 0 )
        {
            return null;
        }
        String[] result = new String[variables.size()];
        for ( int i = 0; i < result.length; i++ )
        {
            result[i] = ( (Variable) variables.elementAt( i ) ).getContent();
        }
        return result;
    }
}
