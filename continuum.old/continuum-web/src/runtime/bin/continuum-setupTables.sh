#!/bin/bash

THIS_PROG="$0"
PRGDIR=`dirname "$THIS_PROG"`
basedir=`cd "$PRGDIR/.." ; pwd`

CP=src/main/resources:apps/continuum/resources:target/classes

#for jar in `find $basedir -path '*/apps/*/lib/*.jar' -or -path '*/core/*.jar'|sort`
#do
#  CP=$CP:$jar
#done

CP=$CP:`find $basedir -name '*.jar' -printf "%p:"`

java -classpath $CP org.codehaus.continuum.standalone.SetupTables "$@"
