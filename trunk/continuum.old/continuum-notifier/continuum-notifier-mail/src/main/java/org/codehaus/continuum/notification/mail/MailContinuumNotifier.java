package org.codehaus.continuum.notification.mail;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.maven.model.CiManagement;
import org.apache.maven.project.MavenProject;

import org.codehaus.continuum.ContinuumException;
import org.codehaus.continuum.builder.maven2.Maven2ProjectDescriptor;
import org.codehaus.continuum.mail.MailMessage;
import org.codehaus.continuum.notification.ContinuumNotifier;
import org.codehaus.continuum.project.BuildResult;
import org.codehaus.continuum.project.ContinuumProject;
import org.codehaus.continuum.utils.PlexusUtils;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: MailContinuumNotifier.java,v 1.2 2004-07-08 01:12:47 trygvis Exp $
 */
public class MailContinuumNotifier
    extends AbstractLogEnabled
    implements Initializable, ContinuumNotifier
{
    /**
     * The hostname of the SMTP server.
     * 
     * @default localhost
     */
    private String smtpServer;

    /**
     * If set; all emails will be send to this address. If not all the nag email
     * address from the pom will be used.
     * 
     * @default
     */
    private String to;

    /**
     * If set; all emails will be send from this address. If not all the nag email
     * address from the pom will be used.
     * 
     * @default
     */
    private String from;

    /**
     * 
     * @default 25
     */
    private Integer smtpPort;

    // members

    private int port;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws Exception
    {
        PlexusUtils.assertConfiguration( smtpServer, "smtp-server" );

        if ( to == null )
        {
            getLogger().info( "To address is not configured, will use the nag email address from the project." );
        }

        if ( from == null )
        {
            getLogger().info( "From address is not configured, will use the nag email address from the project." );
        }

        if ( smtpPort == null )
        {
            port = 25;
            getLogger().info( "Smtp port is not configured, will use port 25." );
        }
        else
        {
            port = smtpPort.intValue();
            getLogger().info( "Smtp port: " + port );
        }
    }

    // ----------------------------------------------------------------------
    // Notifier Implementation
    // ----------------------------------------------------------------------

    public void buildStarted( BuildResult build )
        throws ContinuumException
    {
    }

    public void checkoutStarted( BuildResult build )
        throws ContinuumException
    {
    }

    public void checkoutComplete( BuildResult build, Exception ex)
        throws ContinuumException
    {
    }

    public void runningGoals( BuildResult build )
        throws ContinuumException
    {
    }

    public void goalsCompleted( BuildResult build, Exception ex)
        throws ContinuumException
    {
    }

    public void buildComplete( BuildResult build, Exception ex)
        throws ContinuumException
    {
        StringWriter message = new StringWriter();

        PrintWriter output = new PrintWriter( message );

        if ( ex == null )
        {
            output.println( "Build successfull." );
        }
        else
        {
            output.println( "Build failed." );
            output.println();
            ex.printStackTrace( output );
        }

        output.close();

        sendMessage( build.getProject(), message.toString() );
    }

    // ----------------------------------------------------------------------
    // Private
    // ----------------------------------------------------------------------

    private void sendMessage( ContinuumProject project, String message )
        throws ContinuumException
    {
        getLogger().info( "Sending message to: " + smtpServer + ":" + port );

        MavenProject mavenProject = getMavenProject( project );

        try
        {
            MailMessage mailMessage = new MailMessage( smtpServer, port );

            String from = getFromAddress( mavenProject );

            if ( from == null )
            {
                getLogger().warn( mavenProject.getGroupId() + ":" + mavenProject.getArtifactId() + ": Project is missing nag email and global from address is missing." );

                return;
            }

            mailMessage.from( from );

            String to = getToAddress( mavenProject );

            if ( to == null )
            {
                getLogger().warn( mavenProject.getGroupId() + ":" + mavenProject.getArtifactId() + ": Project is missing nag email and global from address is missing." );

                return;
            }

            mailMessage.to( to );

            mailMessage.setSubject( "[continuum] " + project.getName() );

            mailMessage.getPrintStream().print( message );

//            mailMessage.sendAndClose();

            // TODO: remove me
            getLogger().info( "The following message has been sent: " );

            getLogger().info( message );
        }
        catch( IOException ex )
        {
            throw new ContinuumException( "Exception while sending message.", ex );
        }
    }

    private String getFromAddress( MavenProject mavenProject )
    {
        String address;

        if ( from != null )
        {
            return from;
        }

        CiManagement ciManagement = mavenProject.getCiManagement();

        if ( ciManagement == null )
        {
            return null;
        }

        address = StringUtils.trim( ciManagement.getNagEmailAddress() );

        if ( StringUtils.isEmpty( address ) )
        {
            return null;
        }

        return address;
    }

    private String getToAddress( MavenProject mavenProject )
    {
        String address;

        if ( to != null )
        {
            return to;
        }

        address = StringUtils.trim( mavenProject.getCiManagement().getNagEmailAddress() );

        if ( StringUtils.isEmpty( address ) )
        {
            return null;
        }

        return address;
    }

    private MavenProject getMavenProject( ContinuumProject project )
        throws ContinuumException
    {
        if ( !project.getType().equals( "maven2" ) )
        {
            throw new ContinuumException( "Uknown project type: '" + project.getType() + "'." );
        }

        Maven2ProjectDescriptor descriptor = (Maven2ProjectDescriptor) project.getDescriptor();

        return descriptor.getMavenProject();
    }
}
