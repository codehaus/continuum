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
    m2 $goals
    cd ..
  )
}

find . -name target -type d | xargs rm -rf

m2 pom:install
build "continuum-core"
build "continuum-hibernate-store"
#build "continuum-cli"
build "continuum-mail-notifier"
build "continuum-standalone"
#build "continuum-xmlrpc-server"
#build "continuum-xmlrpc-client"

(
  cd continuum-standalone
  sh make.sh
  cd ..
)
