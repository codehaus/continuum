package org.codehaus.continuum.test.scm;

/*
 * LICENSE
 */

import org.apache.maven.scm.ScmException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: InvalidConnectionUrlException.java,v 1.1 2004-09-07 16:22:19 trygvis Exp $
 */
public class InvalidConnectionUrlException
    extends ScmException
{
    public InvalidConnectionUrlException( String msg )
    {
        super( msg );
    }
}
