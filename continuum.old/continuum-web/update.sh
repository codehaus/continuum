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
cp ../continuum-test/target/*.jar $lib
cp ../continuum-alarm-trigger/target/*.jar $lib
#cp ../continuum-cli/target/*.jar $lib
cp ../continuum-core/target/*.jar $lib
#cp ../continuum-fbi-trigger/target/*.jar $lib
cp ../continuum-hibernate-store/target/*.jar $lib
cp ../continuum-mail-notifier/target/*.jar $lib
#cp ../continuum-standalone/target/*.jar $lib
#cp ../continuum-tools/target/*.jar $lib
#cp ../continuum-xmlrpc-client/target/*.jar $lib
#cp ../continuum-xmlrpc-server/target/*.jar $lib
