package org.codehaus.plexus.continuum;

import org.apache.maven.project.MavenProject;

public interface Continuum
{
    String ROLE = Continuum.class.getName();

    void addProject( MavenProject project );

    void addProject( String url );

    void buildProject( String groupId, String artifactId )
        throws Exception;

    void buildProjects()
        throws Exception;
}
