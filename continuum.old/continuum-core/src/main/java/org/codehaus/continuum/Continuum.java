package org.codehaus.plexus.continuum;

import org.apache.maven.project.Project;

public interface Continuum
{
    String ROLE = Continuum.class.getName();

    void addProject( Project project );
}

