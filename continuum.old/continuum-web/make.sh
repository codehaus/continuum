#!/bin/bash

# rm -rf generated-runtime
# rm -rf target

m2 jar:install plexus:runtime
ret=$?; if [ $ret != 0 ]; then exit $ret; fi

sh update.sh
ret=$?; if [ $ret != 0 ]; then exit $ret; fi
