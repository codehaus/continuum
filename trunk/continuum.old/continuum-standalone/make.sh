#!/bin/bash

if [ ! -d ${M2_HOME} ]
then
  echo "\$M2_HOME must point to a directory"
  exit -1
fi

runtime=generated-runtime

# Build the runtime
m2 plexus:runtime

sh update.sh

sh package.sh
