#!/bin/bash

# Install the POMs
( m2 pom:install )
( cd continuum-store && m2 pom:install )
( cd continuum-notifier && m2 pom:install )
( cd continuum-trigger && m2 pom:install )
( cd continuum-builder && m2 pom:install )
# m2 -r -Dmaven.reactor.excludes=continuum-sandbox/**,**/apps/** pom:install
# m2 -r -Dmaven.reactor.excludes=continuum-sandbox/**,**/apps/** eclipse:eclipse

# Build the JARs
( cd continuum-api && m2 jar:install )
( cd continuum-test && m2 jar:install )
( cd continuum-core && m2 jar:install )
( cd continuum-builder/continuum-builder-maven2 && m2 jar:install )
( cd continuum-store/continuum-store-hibernate && m2 jar:install )
( cd continuum-store/continuum-store-hibernate-it && m2 jar:install )
( cd continuum-store/continuum-store-prevayler && m2 jar:install )
( cd continuum-notifier/continuum-notifier-mail && m2 jar:install )
( cd continuum-trigger/continuum-trigger-alarm && m2 jar:install )
( cd continuum-trigger/continuum-trigger-socket && m2 jar:install )
( cd continuum-standalone && m2 jar:install )
( cd continuum-web && m2 jar:install )
( cd continuum-it && m2 jar:install )

(
  cd continuum-web
  sh runtime.sh
)

#( cd continuum-it && m2 jar:install )

# m2 -r -Dmaven.reactor.excludes=continuum-sandbox/** eclipse:eclipse
# m2 -r -Dmaven.reactor.excludes=continuum-sandbox/** pom:install
