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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.continuum.notification.ContinuumNotificationDispatcher;
import org.apache.maven.continuum.project.ContinuumBuild;
import org.apache.maven.continuum.project.ContinuumProject;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.mailsender.test.SmtpServer;
import org.codehaus.plexus.notification.notifier.Notifier;
import org.codehaus.plexus.util.CollectionUtils;

import com.dumbster.smtp.SmtpMessage;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: MailContinuumNotifierTest.java,v 1.1.1.1 2005-03-29 20:42:04 trygvis Exp $
 */
public class MailContinuumNotifierTest
    extends PlexusTestCase
{
    public void testMailNotification()
        throws Exception
    {
        Notifier notifier = (Notifier) lookup( Notifier.ROLE, "mail" );

        SmtpServer smtpServer = (SmtpServer) lookup( SmtpServer.ROLE );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        String source = ContinuumNotificationDispatcher.MESSAGE_ID_BUILD_COMPLETE;

        Set recipients = new HashSet();

        recipients.add( "foo@bar" );

        Map context =  new HashMap();

        ContinuumProject project = new ContinuumProject();

        project.setName( "Test Project" );

        project.setBuilderId( "maven2" );

        context.put( ContinuumNotificationDispatcher.CONTEXT_PROJECT, project );

        ContinuumBuild build = new ContinuumBuild();

        build.setId( "17" );

        context.put( ContinuumNotificationDispatcher.CONTEXT_BUILD, build );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        notifier.sendNotification( source, recipients, context );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        assertEquals( 1, smtpServer.getReceievedEmailSize() );

        List mails = CollectionUtils.iteratorToList( smtpServer.getReceivedEmail() );

        SmtpMessage mailMessage = (SmtpMessage) mails.get( 0 );

        assertEquals( "Continuum<continuum@localhost>", mailMessage.getHeaderValue( "From" ) );

        assertEquals( "foo@bar<foo@bar>", mailMessage.getHeaderValue( "To") );

//        System.err.println( mailMessage.getBody() );
    }
}
