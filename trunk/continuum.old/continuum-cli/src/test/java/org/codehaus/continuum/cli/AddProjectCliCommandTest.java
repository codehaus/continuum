package org.codehaus.continuum.cli;

/*
 * LICENSE
 */

import java.util.Iterator;

import junit.framework.TestCase;

import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.store.ContinuumStore;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AddProjectCliCommandTest.java,v 1.1 2004-07-07 05:05:36 trygvis Exp $
 */
public class AddProjectCliCommandTest
    extends TestCase
{
    public void testExecution()
        throws Exception
    {
        String[] args = new String[]{
            "addProject",
            "--project-name=Foo Project",
            "--scm-connection=scm:cvs:local:ignored:/cvsroot/:cvs-module",
            "--project-type=maven2"
        };

        ContinuumCC cli = new ContinuumCC();

        cli.start();

        ContinuumStore store = (ContinuumStore) cli.getPlexus().lookup( ContinuumStore.ROLE );

        Iterator it = store.getAllProjects();

        assertFalse( it.hasNext() );

        int exitCode = cli.work( args );

        assertEquals( 0, exitCode );

        it = store.getAllProjects();

        assertTrue( it.hasNext() );

        ContinuumProject project = (ContinuumProject) it.next();

        assertEquals( "Foo Project", project.getName() );

        assertEquals( "scm:cvs:local:ignored:/cvsroot/:cvs-module", project.getScmConnection() );

        assertEquals( "maven2", project.getType() );

        assertFalse( it.hasNext() );
    }
}
