package org.codehaus.continuum.utils;

/*
 * LICENSE
 */

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ContinuumUtils.java,v 1.1 2004-10-07 12:08:57 trygvis Exp $
 */
public class ContinuumUtils
{
    public static String getExceptionStackTrace( Throwable ex )
    {
        StringWriter string = new StringWriter();

        PrintWriter writer = new PrintWriter( string );

        ex.printStackTrace( writer );

        writer.flush();

        return string.getBuffer().toString();
    }
}
