package org.apache.maven.continuum.notification.mail;

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

import junit.framework.TestCase;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: FormatterToolTest.java,v 1.1 2005-04-01 22:55:53 trygvis Exp $
 */
public class FormatterToolTest
    extends TestCase
{
    public void testIntervalFormatting()
        throws Exception
    {
        FormatterTool tool = new FormatterTool( null );

        assertEquals( "10s", tool.formatInterval( 0, 10 ) );

        assertEquals( "1m 0s", tool.formatInterval( 0, 1 * 60 + 0 ) );

        assertEquals( "1m 10s", tool.formatInterval( 0, 1 * 60 + 10 ) );

        assertEquals( "1h 0m 0s", tool.formatInterval( 0, 1 * 3600 + 0 * 60 + 0 ) );

        assertEquals( "1h 10m 0s", tool.formatInterval( 0, 1 * 3600 + 10 * 60 + 0 ) );

        assertEquals( "1h 1m 10s", tool.formatInterval( 0, 1 * 3600 + 1 * 60 + 10 ) );
    }
}
