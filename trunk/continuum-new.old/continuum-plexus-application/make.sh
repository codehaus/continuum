#!/bin/bash

set -e

rm -rf target/plexus-application
m2 install plexus:app plexus:bundle-application
rm target/plexus-application/lib/plexus-container-api-1.0-alpha-1-SNAPSHOT.jar
rm target/plexus-application/lib/jetty-4.2.10.jar
rm target/plexus-application/lib/servletapi-2.3.jar
rm target/plexus-application/lib/geronimo-spec-servlet-2.4-rc2.jar
rm target/plexus-application/lib/org.mortbay.jetty-4.2.22.jar
mv target/plexus-application/lib/continuum-web-1.0-alpha-1-SNAPSHOT.jar target/plexus-application/lib/continuum-web-1.0-alpha-1-SNAPSHOT.war
jar cf target/continuum-plexus-application-1.0-alpha-1-SNAPSHOT-application.jar -C target/plexus-application/ .
