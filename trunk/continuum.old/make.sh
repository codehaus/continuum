#!/bin/bash

function build
{
  project=$1
  goals=$2

  if [ -z "$goals" ]
  then
    goals="clean:clean jar:install"
  fi

  (
    cd $project
    echo "-----------------------------------------------------------------"
    echo "BUILDING " $project
    echo "-----------------------------------------------------------------"
    m2 $goals
    ret=$?; if [ $ret != 0 ]; then exit $ret; fi
    cd ..
  )
}

find . -name target -type d | xargs rm -rf

m2 pom:install
ret=$?; if [ $ret != 0 ]; then exit $ret; fi
# m2 -r -Dmaven.reactor.includes=*/pom.xml install:install
# m2 -r -Dmaven.reactor.includes=pom.xml,*/pom.xml pom:install

build "continuum-api"
build "continuum-test"
build "continuum-core"
build "continuum-hibernate-store"
#build "continuum-cli"
build "continuum-mail-notifier"
build "continuum-alarm-trigger"
#build "continuum-standalone"
#build "continuum-xmlrpc-server"
#build "continuum-xmlrpc-client"

(
#  cd continuum-standalone
  cd continuum-web
  sh make.sh
  cd ..
)

build "continuum-it"
