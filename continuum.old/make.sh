#!/bin/bash

( m2 pom:install )
( cd continuum-api && m2 jar:install )
( cd continuum-test && m2 jar:install )
( cd continuum-core && m2 jar:install )
( cd continuum-builder && m2 pom:install )
( cd continuum-builder/continuum-builder-maven2 && m2 jar:install )
( cd continuum-store/continuum-store-hibernate && m2 jar:install )
( cd continuum-notifier/continuum-notifier-mail && m2 jar:install )
( cd continuum-trigger/continuum-trigger-alarm && m2 jar:install )

(
#  cd continuum-web
#  sh make.sh
#  cd ..
)

#( cd continuum-it && m2 jar:install )

# m2 -r -Dmaven.reactor.excludes=continuum-sandbox/** eclipse:eclipse
# m2 -r -Dmaven.reactor.excludes=continuum-sandbox/** pom:install
