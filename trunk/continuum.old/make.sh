#!/bin/bash

if [ ! -x ""`which m2` ]
then
  echo "m2 has to be in the PATH"
  exit
fi

# Install the POMs
( m2 pom:install )
m2 -r -Dmaven.reactor.includes=\
continuum-store/pom.xml,\
continuum-notifier/pom.xml,\
continuum-trigger/pom.xml,\
continuum-builder/pom.xml pom:install

# m2 -r -Dmaven.reactor.excludes=continuum-sandbox/**,**/apps/** pom:install
# m2 -r -Dmaven.reactor.excludes=continuum-sandbox/**,**/apps/** eclipse:eclipse
# m2 -r -Dmaven.reactor.excludes=continuum-sandbox/**,**/apps/** idea:idea

# Build the JARs
( cd continuum-api && m2 jar:install )
( cd continuum-test && m2 jar:install )
( cd continuum-maven-utils && m2 jar:install )
( cd continuum-core && m2 jar:install )
( cd continuum-builder/continuum-builder-maven2 && m2 jar:install )
( cd continuum-builder/continuum-builder-shell && m2 jar:install )
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
