package org.codehaus.continuum.notification.mail;

/*
 * LICENSE
 */

import java.io.PrintWriter;

import org.codehaus.plexus.logging.Logger;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AbstractMailGenerator.java,v 1.1 2004-10-06 14:09:44 trygvis Exp $
 */
public abstract class AbstractMailGenerator
    implements MailGenerator
{
    private Logger logger;

    public AbstractMailGenerator( Logger logger )
    {
        this.logger = logger;
    }

    protected Logger getLogger()
    {
        return logger;
    }

    protected void line( PrintWriter output )
    {
//        output.println( "----------------------------------------------------------------------------" );
        output.println( "****************************************************************************" );
    }
}
