#!/bin/bash

set -e

rm -rf target/plexus-runtime
m2 plexus:runtime
cp target/*-application.jar target/plexus-runtime/apps/
sh target/plexus-runtime/bin/plexus.sh
