#!/bin/bash

set -e

if [ ! -x "`which m2`" ]
then
  echo "m2 has to be in the PATH"
  exit
fi

m2 pom:install

# Build the JARs
( cd continuum-core && sh make.sh )
( cd continuum-web && m2 install )

(
  cd continuum-web
  sh runtime.sh
)

exit;

# Build the Plexus application
(
  cd continuum-plexus-application
#  m2 plexus:app plexus:bundle-application
  sh make.sh
)

# Build the plexus runtime and install the application
(
  cd continuum-runtime
  rm -rf target/plexus-runtime
  m2 plexus:runtime
  cp ../continuum-plexus-application/target/*-application.jar target/plexus-runtime/apps
  cp /home/trygvis/dev/org.codehaus.plexus/plexus-services/plexus-service-jetty/target/*-service.jar target/plexus-runtime/services

  # TODO: Package the runtime with the application
)
