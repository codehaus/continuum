#!/bin/bash

function findRepo()
{
  repo="$HOME/maven-repo-local"

  if [ ! -d "$repo" ]
  then
    echo "FATAL: Could not find a maven repository in $repo"
    exit
  fi
}

function findM2()
{
  m2="$HOME/m2"

  if [ ! -x $m2/bin/m2 ]
  then
    echo "FATAL: Could not find a maven 2 installation in $m2"
    exit
  fi
}

function copyDependency
{
  groupId="$1"
  artifactId="$2"
  version="$3"

  mkdir -p "$dest"
  file="$repo/$groupId/jars/$artifactId-$version.jar"

  if [ ! -r "$file" ]
  then
    echo "FATAL: Could not find $file"
    exit -1
  fi

  destFile="$dest/$artifactId-$version.jar" 

  if [ "$file" -nt "$destFile" ]
  then
    echo "Copying $groupId $artifactId $version"
    cp "$file" "$destFile"
  fi 
}

function copyPlugin
{
  groupId="$1"
  artifactId="$2"
  version="$3"

  mkdir -p "$dest"
  file="$repo/$groupId/plugins/$artifactId-$version.jar"

  if [ ! -r "$file" ]
  then
    echo "FATAL: Could not find $file"
    exit -1
  fi

  destFile="$runtime/apps/maven2/repository/$groupId/plugins/$artifactId-$version.jar"

  if [ "$file" -nt "$destFile" ]
  then
    echo "Copying plugin: $artifactId $version"
    mkdir -p "$runtime/apps/maven2/repository/$groupId/plugins/"
    cp "$file" "$destFile"
  fi 
}

findRepo;
findM2;

runtime="generated-runtime"
continuum="$runtime/apps/continuumweb"
maven2="$runtime/apps/maven2"

#
# Make the runtime
#

dest="$runtime/core"
#copyDependency "plexus" "plexus-container-api" "1.0-alpha-1-SNAPSHOT"
copyDependency "plexus" "plexus-container-default" "1.0-alpha-2-SNAPSHOT"
copyDependency "plexus" "plexus-container-artifact" "1.0-alpha-1-SNAPSHOT"
copyDependency "plexus" "plexus-utils" "1.0-alpha-1-SNAPSHOT"
copyDependency "maven" "maven-artifact" "2.0-SNAPSHOT"

mkdir -p $runtime/bin
mkdir -p $runtime/conf
cp src/runtime/bin/plexus.sh $runtime/bin

cp src/runtime/conf/plexus.conf $runtime/conf
cp src/runtime/conf/classworlds.conf $runtime/conf

dest=$runtime/core/boot
copyDependency "classworlds" "classworlds" "1.1-SNAPSHOT"

#
# Make the continuumweb application
#

mkdir -p $continuum

mkdir -p $runtime/apps/continuumweb/localization
mkdir -p $runtime/apps/continuumweb/resources

# Copy the resources
cp -r src/main/resources/* $runtime/apps/continuumweb/resources 

# make some special links for the webapp
(
  cd $runtime/apps/continuumweb 
  echo app:$runtime/apps/continuumweb 
  rm -rf web && ln -s ../../../src/main/resources/web web
  rm -rf forms && ln -s ../../../src/main/resources/forms forms
  rm -rf localization && ln -s ../../../src/main/resources/localization localization
)

# Copy the dependencies
dest=$runtime/apps/continuumweb/lib
copyDependency "continuum" "continuum-core" "1.0-alpha-1-SNAPSHOT"
copyDependency "continuum" "continuum-web" "1.0-alpha-1-SNAPSHOT"

copyDependency "plexus" "plexus-formica" "1.0-beta-3-SNAPSHOT"
copyDependency "plexus" "plexus-formica-web" "1.0-alpha-1-SNAPSHOT"
copyDependency "plexus" "plexus-summit" "1.0-beta-4-SNAPSHOT"
copyDependency "plexus" "plexus-i18n" "1.0-beta-3"
copyDependency "plexus" "plexus-mail-sender-api" "1.0-alpha-1-SNAPSHOT"
copyDependency "plexus" "plexus-mail-sender-simple" "1.0-alpha-1-SNAPSHOT"
copyDependency "plexus" "plexus-jetty" "1.0-beta-2-SNAPSHOT"
copyDependency "plexus" "plexus-log4j-logging" "1.0-SNAPSHOT"
copyDependency "plexus" "plexus-servlet" "1.0-beta-2-SNAPSHOT"
copyDependency "plexus" "plexus-velocity" "1.0-beta-4-SNAPSHOT"

copyDependency "maven" "maven-scm-api" "1.0-alpha-1-SNAPSHOT"
copyDependency "maven" "maven-scm-provider-cvs" "1.0-alpha-1-SNAPSHOT"

copyDependency "log4j" "log4j" "1.2.8"
copyDependency "jetty" "org.mortbay.jetty" "4.2.22"
copyDependency "commons-fileupload" "commons-fileupload" "1.0"
copyDependency "commons-collections" "commons-collections" "2.0"
copyDependency "qdox" "qdox" "1.2"
copyDependency "servletapi" "servletapi" "2.3"
copyDependency "velocity" "velocity" "1.4"
copyDependency "obie" "stash" "1.0-alpha-1-SNAPSHOT"
copyDependency "jdbm" "jdbm" "0.20"
copyDependency "oro" "oro" "2.0.7"
copyDependency "ognl" "ognl" "2.5.1"

# For the Prevayler store
copyDependency "prevayler" "prevayler" "2.02.005"

mkdir -p $maven2
mkdir -p $runtime/apps/maven2
m2=$HOME/m2/

if [ ! -d $m2 ]
then
  echo "FATAL: $m2 doesn't exist"
  exit
fi

cp -r $m2/* $runtime/apps/maven2
mkdir -p $runtime/apps/maven2/repository

copyPlugin "maven" "maven-surefire-plugin" "1.0-SNAPSHOT"
copyPlugin "maven" "maven-jar-plugin" "1.0-SNAPSHOT"

exit
