#!/bin/bash

function findRepo()
{
  repo="$HOME/.m2/repository"

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
copyDependency "plexus" "plexus-container-api" "1.0-alpha-1-SNAPSHOT"
copyDependency "plexus" "plexus-container-default" "1.0-alpha-1-SNAPSHOT"
copyDependency "plexus" "plexus-container-artifact" "1.0-alpha-1-SNAPSHOT"
copyDependency "plexus" "plexus-utils" "1.0-alpha-1-SNAPSHOT"
copyDependency "maven" "maven-artifact" "2.0-SNAPSHOT"

mkdir -p $runtime/bin
mkdir -p $runtime/conf
cp src/runtime/bin/plexus.sh $runtime/bin
cp src/runtime/bin/continuum-setupTables.sh $runtime/bin
cp src/runtime/bin/continuum-addProject.sh $runtime/bin

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
copyDependency "bcel" "bcel" "5.0"
copyDependency "cglib" "cglib" "1.0"
copyDependency "commons-beanutils" "commons-beanutils" "1.6"
#copyDependency "commons-cli" "commons-cli" "1.0"
copyDependency "commons-collections" "commons-collections" "3.0"
#copyDependency "commons-httpclient" "commons-httpclient" "2.0-rc2"
copyDependency "commons-lang" "commons-lang" "1.0.1"
copyDependency "commons-logging" "commons-logging" "1.0.2"
copyDependency "continuum" "continuum-api" "1.0-alpha-1-SNAPSHOT"
copyDependency "continuum" "continuum-builder-maven2" "1.0-alpha-1-SNAPSHOT"
copyDependency "continuum" "continuum-builder-shell" "1.0-alpha-1-SNAPSHOT"
copyDependency "continuum" "continuum-core" "1.0-alpha-1-SNAPSHOT"
copyDependency "continuum" "continuum-maven-utils" "1.0-alpha-1-SNAPSHOT"
copyDependency "continuum" "continuum-notifier-mail" "1.0-alpha-1-SNAPSHOT"
copyDependency "continuum" "continuum-standalone" "1.0-alpha-1-SNAPSHOT"
copyDependency "continuum" "continuum-store-hibernate" "1.0-alpha-1-SNAPSHOT"
copyDependency "continuum" "continuum-store-prevayler" "1.0-alpha-1-SNAPSHOT"
copyDependency "continuum" "continuum-test" "1.0-alpha-1-SNAPSHOT"
copyDependency "continuum" "continuum-trigger-alarm" "1.0-alpha-1-SNAPSHOT"
copyDependency "continuum" "continuum-trigger-socket" "1.0-alpha-1-SNAPSHOT"
copyDependency "continuum" "continuum-web" "1.0-alpha-1-SNAPSHOT"
copyDependency "dom4j" "dom4j" "1.4"
copyDependency "geronimo-spec" "geronimo-spec-jta" "DEV"
copyDependency "hibernate" "hibernate" "2.0.3"
copyDependency "hsqldb" "hsqldb" "1.7.2.2"
copyDependency "jdom" "jdom" "b10"
#copyDependency "jetty" "jetty" "4.2.10"
copyDependency "jetty" "org.mortbay.jetty" "4.2.22"
copyDependency "log4j" "log4j" "1.2.8"
#copyDependency "marmalade" "marmalade-core" "0.1"
#copyDependency "marmalade" "marmalade-el-ognl" "0.1"
copyDependency "maven" "maven-core" "2.0-SNAPSHOT"
copyDependency "maven" "maven-model" "2.0-SNAPSHOT"
copyDependency "maven" "maven-artifact" "2.0-SNAPSHOT"
copyDependency "maven" "maven-plugin" "2.0-SNAPSHOT"
#copyDependency "maven" "scm-api" "0.9-SNAPSHOT"
#copyDependency "maven" "scm-cvslib" "0.9-SNAPSHOT"

copyDependency "maven" "maven-scm-api" "1.0-alpha-1-SNAPSHOT"
copyDependency "maven" "maven-scm-provider-cvs" "1.0-alpha-1-SNAPSHOT"
copyDependency "maven" "maven-scm-provider-local" "1.0-alpha-1-SNAPSHOT"
copyDependency "maven" "wagon-api" "1.0-alpha-1-SNAPSHOT"
copyDependency "maven" "wagon-http-lightweight" "1.0-alpha-1-SNAPSHOT"
copyDependency "modello" "modello-1.0" "SNAPSHOT"
copyDependency "odmg" "odmg" "3.0"
copyDependency "ognl" "ognl" "2.5.1"
copyDependency "oro" "oro" "2.0.6"
copyDependency "plexus" "plexus-compiler-api" "1.0"
copyDependency "plexus" "plexus-compiler-javac" "1.0"

#copyDependency "plexus" "plexus-formica" "1.0-beta-2"
#copyDependency "plexus" "plexus-summit" "1.0-beta-3"
#copyDependency "commons-fileupload" "commons-fileupload" "1.0-beta-1"

copyDependency "plexus" "plexus-formica" "1.0-beta-3-SNAPSHOT"
copyDependency "plexus" "plexus-formica-web" "1.0-alpha-1-SNAPSHOT"
copyDependency "plexus" "plexus-summit" "1.0-beta-4-SNAPSHOT"
copyDependency "commons-fileupload" "commons-fileupload" "1.0"

copyDependency "plexus" "plexus-hibernate" "1.0-beta-4-SNAPSHOT"
copyDependency "plexus" "plexus-i18n" "1.0-beta-3"
copyDependency "plexus" "plexus-mail-sender-api" "1.0-alpha-1-SNAPSHOT"
copyDependency "plexus" "plexus-mail-sender-simple" "1.0-alpha-1-SNAPSHOT"
copyDependency "plexus" "plexus-jetty" "1.0-beta-2-SNAPSHOT"
copyDependency "plexus" "plexus-log4j-logging" "1.0-SNAPSHOT"
copyDependency "plexus" "plexus-servlet" "1.0-beta-2-SNAPSHOT"
copyDependency "plexus" "plexus-velocity" "1.0-beta-4-SNAPSHOT"
copyDependency "postgresql" "postgresql" "7.4.1-jdbc3"
copyDependency "qdox" "qdox" "1.2"
copyDependency "servletapi" "servletapi" "2.3"
#copyDependency "surefire" "surefire" "1.1"
#copyDependency "surefire" "surefire-booter" "1.1"
copyDependency "tomcat" "jasper-compiler" "4.0.4"
copyDependency "tomcat" "jasper-runtime" "4.0.4"
copyDependency "velocity" "velocity" "1.4"
#copyDependency "werken-xpath" "werken-xpath" "0.9.4"
copyDependency "xpp3" "xpp3" "1.1.3.3"
copyDependency "xstream" "xstream" "1.0-SNAPSHOT"

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

#$runtime/apps/continuumweb/plugins/maven-clean-plugin-1.0-SNAPSHOT.jar
#$runtime/apps/continuumweb/plugins/maven-compiler-plugin-1.0-SNAPSHOT.jar
#$runtime/apps/continuumweb/plugins/maven-eclipse-plugin-1.0-SNAPSHOT.jar
#$runtime/apps/continuumweb/plugins/maven-install-plugin-1.0-SNAPSHOT.jar
#$runtime/apps/continuumweb/plugins/maven-jar-plugin-1.0-SNAPSHOT.jar
#$runtime/apps/continuumweb/plugins/maven-plugin-plugin-1.0-SNAPSHOT.jar
#$runtime/apps/continuumweb/plugins/maven-pom-plugin-1.0-SNAPSHOT.jar
#$runtime/apps/continuumweb/plugins/maven-resources-plugin-1.0-SNAPSHOT.jar
#$runtime/apps/continuumweb/plugins/maven-surefire-plugin-1.0-SNAPSHOT.jar
$runtime/apps/continuumweb/resources/conf/config.properties
$runtime/apps/continuumweb/resources/conf/plexus.conf
$runtime/apps/continuumweb/resources/conf/web-view.xml
$runtime/apps/continuumweb/resources/forms/addProjectPomUrl.xml
$runtime/apps/continuumweb/resources/forms/addProjectScm.xml
$runtime/apps/continuumweb/resources/forms/issue.xml
$runtime/apps/continuumweb/resources/forms/login.xml
$runtime/apps/continuumweb/resources/forms/user.xml
$runtime/apps/continuumweb/resources/hibernate.cfg.xml
$runtime/apps/continuumweb/resources/localization/Continuum.properties
$runtime/apps/continuumweb/resources/localization/foo.jar
$runtime/apps/continuumweb/resources/META-INF/plexus/components.xml
$runtime/apps/continuumweb/resources/META-INF/plexus/plexus.conf
$runtime/apps/continuumweb/resources/META-INF/plexus/web-view.xml
$runtime/apps/continuumweb/resources/org/codehaus/continuum/Continuum.properties
$runtime/apps/continuumweb/resources/web/continuumweb/WEB-INF/web.xml

dest=$runtime/apps/continuumweb/lib
$runtime/apps/maven2/bin/classworlds.conf
$runtime/apps/maven2/bin/m2
$runtime/apps/maven2/bin/m2.bat
$runtime/apps/maven2/core/classworlds-1.1-SNAPSHOT.jar
$runtime/apps/maven2/core/plexus-0.17-SNAPSHOT.jar
$runtime/apps/maven2/core/xpp3-1.1.3.3.jar
$runtime/apps/maven2/core/xstream-1.0-SNAPSHOT.jar
$runtime/apps/maven2/lib/commons-cli-1.0-beta-2.jar
$runtime/apps/maven2/lib/maven-artifact-2.0-SNAPSHOT.jar
$runtime/apps/maven2/lib/maven-core-2.0-SNAPSHOT.jar
$runtime/apps/maven2/lib/maven-model-2.0-SNAPSHOT.jar
$runtime/apps/maven2/lib/maven-plugin-2.0-SNAPSHOT.jar
$runtime/apps/maven2/lib/ognl-2.5.1.jar
$runtime/apps/maven2/lib/plexus-artifact-container-1.0-alpha-1-SNAPSHOT.jar
$runtime/apps/maven2/lib/plexus-i18n-1.0-beta-3.jar
$runtime/apps/maven2/lib/wagon-api-1.0-alpha-1-SNAPSHOT.jar
$runtime/apps/maven2/lib/wagon-http-lightweight-1.0-alpha-1-SNAPSHOT.jar
$runtime/apps/maven2/override.xml
$runtime/apps/maven2/repository
$runtime/bin/plexus.bat
$runtime/bin/plexus.sh
$runtime/conf/classworlds.conf
$runtime/conf/config.properties
$runtime/conf/plexus.conf
$runtime/conf/web-view.xml
$runtime/conf/wrapper.conf
$runtime/core/asm-1.3.4.jar
$runtime/core/boot/classworlds-1.1-SNAPSHOT.jar
$runtime/core/junit-3.8.1.jar
$runtime/core/plexus-0.17-SNAPSHOT.jar
$runtime/core/plexus-log4j-logging-1.0-SNAPSHOT.jar
$runtime/core/wrapper.jar
$runtime/core/xpp3-1.1.3.3.jar
$runtime/core/xstream-1.0-SNAPSHOT.jar
$runtime/logs/
$runtime/temp/
