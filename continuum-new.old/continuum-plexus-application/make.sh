#!/bin/bash

set -e

rm -rf target/plexus-application
m2 plexus:app 
rm target/plexus-application/lib/jetty-4.2.10.jar
rm target/plexus-application/lib/servletapi-2.3.jar
rm target/plexus-application/lib/geronimo-spec-servlet-2.4-rc2.jar
#mv target/plexus-application/lib/continuum-web-1.0-alpha-1-SNAPSHOT.jar target/plexus-application/lib/continuum-web-1.0-alpha-1-SNAPSHOT.war
#jar cf target/continuum-plexus-application-1.0-alpha-1-SNAPSHOT-application.jar -C target/plexus-application/ .
m2 plexus:bundle-application
