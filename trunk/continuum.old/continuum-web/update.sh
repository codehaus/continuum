#!/bin/bash

runtime=generated-runtime

app=$runtime/apps/continuumweb
lib=$runtime/apps/continuumweb/lib
resources=$runtime/apps/continuumweb/resources
bin=$runtime/bin

standalone=../continuum-standalone

rm -rf $runtime/application

# Copy maven2 into the runtime
#mkdir -p $runtime/apps/maven2/bin
#mkdir -p $runtime/apps/maven2/lib
#mkdir -p $runtime/apps/maven2/plugins
#cp -r $M2_HOME/bin/m2 $runtime/apps/maven2/bin
#cp -r $M2_HOME/lib/*.jar $runtime/apps/maven2/lib
#cp -r $M2_HOME/plugins/*.jar $runtime/apps/maven2/plugins

mkdir -p $runtime/apps/maven2
cp -r $M2_HOME/* $runtime/apps/maven2
cp ~/.maven/repository/maven/jars/maven-core-2.0-SNAPSHOT.jar $runtime/apps/maven2/lib
cp ~/.maven/repository/maven/jars/maven-artifact-2.0-SNAPSHOT.jar $runtime/apps/maven2/core
cp ~/.maven/repository/plexus/jars/plexus-log4j-logging-1.0-SNAPSHOT.jar $runtime/core
cp ~/.maven/repository/maven/jars/maven-artifact-2.0-SNAPSHOT.jar $runtime/apps/maven2/lib
rm -f $runtime/apps/maven2/core/plexus-0.17*.jar
cp ~/.maven/repository/plexus/jars/plexus-0.17-SNAPSHOT.jar $runtime/apps/maven2/lib
cp ~/.maven/repository/plexus/jars/plexus-artifact-container-1.0-alpha-1-SNAPSHOT.jar $runtime/apps/maven2/lib
cp ~/.maven/repository/maven/jars/wagon-api-1.0-alpha-1-SNAPSHOT.jar $runtime/apps/maven2/lib
cp ~/.maven/repository/maven/jars/wagon-http-lightweight-1.0-alpha-1-SNAPSHOT.jar $runtime/apps/maven2/lib

cp ~/.maven/repository/maven/plugins/maven-pom-plugin-1.0-SNAPSHOT.jar $runtime/apps/maven2/repository/maven/plugins/maven-pom-plugin-1.0-SNAPSHOT.jar

cp ~/.maven/repository/plexus/jars/plexus-0.17-SNAPSHOT.jar $runtime/core
cp ~/.maven/repository/plexus/jars/plexus-artifact-container-1.0-alpha-1-SNAPSHOT.jar $runtime/apps/maven2/lib/

cp ~/.maven/repository/classworlds/jars/classworlds-1.1-SNAPSHOT.jar $runtime/core/boot/
cp ~/.maven/repository/classworlds/jars/classworlds-1.1-SNAPSHOT.jar $runtime/apps/maven2/core/

cp ~/.maven/repository/plexus/jars/plexus-compiler-api-1.0.jar $runtime/apps/continuumweb/lib
cp ~/.maven/repository/plexus/jars/plexus-compiler-javac-1.0.jar $runtime/apps/continuumweb/lib
cp ~/.maven/repository/plexus/jars/plexus-compiler-javac-1.0.jar $runtime/apps/continuumweb/lib
cp ~/.maven/repository/plexus/jars/plexus-hibernate-1.0-beta-1.jar $runtime/apps/continuumweb/lib
cp ~/.maven/repository/plexus/jars/plexus-i18n-1.0-beta-3.jar $runtime/apps/continuumweb/lib
cp ~/.maven/repository/plexus/jars/plexus-jetty-1.0-beta-1.jar $runtime/apps/continuumweb/lib
#cp ~/.maven/repository/plexus/jars/plexus-jetty-httpd-1.0-beta-1.jar $runtime/apps/continuumweb/lib
cp ~/.maven/repository/plexus/jars/plexus-servlet-1.0-beta-2-SNAPSHOT.jar $runtime/apps/continuumweb/lib
cp ~/.maven/repository/plexus/jars/plexus-velocity-1.0-beta-3.jar $runtime/apps/continuumweb/lib

cp ~/.maven/repository/plexus/jars/plexus-artifact-container-1.0-alpha-1-SNAPSHOT.jar $runtime/apps/maven2/lib
cp ~/.maven/repository/plexus/jars/plexus-i18n-1.0-beta-3.jar $runtime/apps/maven2/lib

# Web tools
cp ~/.maven/repository/plexus/jars/plexus-formica-1.0-beta-2.jar $runtime/apps/continuumweb/lib
cp ~/.maven/repository/plexus/jars/plexus-summit-1.0-beta-3.jar $runtime/apps/continuumweb/lib
cp ~/.maven/repository/commons-fileupload/jars/commons-fileupload-1.0-beta-1.jar $runtime/apps/continuumweb/lib

#cp ~/.maven/repository/plexus/jars/plexus-formica-1.0-beta-3-SNAPSHOT.jar $runtime/apps/continuumweb/lib
#cp ~/.maven/repository/plexus/jars/plexus-summit-1.0-beta-4-SNAPSHOT.jar $runtime/apps/continuumweb/lib
#cp ~/.maven/repository/commons-fileupload/jars/commons-fileupload-1.0.jar $runtime/apps/continuumweb/lib

# Prevayler deps
cp ~/.maven/repository/prevayler/jars/prevayler-2.02.005.jar $runtime/apps/maven2/lib

chmod +x $runtime/apps/maven2/bin/*

# Fix the classworlds script so that it loads the maven classes
# as a part of the core classes
if [ ! `egrep maven2 $runtime/conf/classworlds.conf|wc -l` -gt 0 ]
then
  echo "  load \${plexus.core}/../apps/maven2/lib/*.jar" >> $runtime/conf/classworlds.conf
  echo "  load \${plexus.core}/../apps/continuumweb/localization" >> $runtime/conf/classworlds.conf
fi

# Copy the resources
mkdir -p $resources
cp -r src/main/resources/* $resources

cp -r $standalone/src/main/bash/continuum-* $bin
cp -r $standalone/src/main/bash/m2 $bin

# make some special links for the webapp
(
  cd $app
  echo app: $app
  rm -rf web && ln -s ../../../src/main/resources/web web
  rm -rf forms && ln -s ../../../src/main/resources/forms forms
  rm -rf localization && ln -s ../../../src/main/resources/localization localization
  cd ../../..
)

#cp -r src/main/bash/continuum-* $bin

cp src/main/resources/conf/plexus.conf $runtime/conf/plexus.conf

cp target/*.jar $lib
cp ../continuum-api/target/*.jar $lib
cp ../continuum-core/target/*.jar $lib
cp ../continuum-builder/continuum-builder-maven2/target/*.jar $lib
cp ../continuum-trigger/continuum-trigger-alarm/target/*.jar $lib
cp ../continuum-store/continuum-store-hibernate/target/*.jar $lib
cp ../continuum-store/continuum-store-prevayler/target/*.jar $lib
cp ../continuum-notifier/continuum-notifier-mail/target/*.jar $lib
cp ../continuum-web/target/*.jar $lib
cp ../continuum-standalone/target/*.jar $lib
cp ../continuum-test/target/*.jar $lib
