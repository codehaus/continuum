#!/bin/bash

runtime=generated-runtime

lib=$runtime/apps/continuum/lib
resources=$runtime/apps/continuum/resources
bin=$runtime/bin

rm -rf $runtime/application

# Copy maven2 into the runtime
mkdir -p $runtime/apps/maven2/lib
mkdir -p $runtime/apps/maven2/plugins
cp -r $M2_HOME/lib/*.jar $runtime/apps/maven2/lib
cp -r $M2_HOME/plugins/*.jar $runtime/apps/maven2/plugins

# Fix the classworlds script so that it loads the maven classes
# as a part of the core classes
if [ ! `egrep maven2 $runtime/conf/classworlds.conf|wc -l` -gt 0 ]
then
  echo "  load \${core}/../apps/maven2/lib/*.jar" >> $runtime/conf/classworlds.conf
fi

# Copy the resources
mkdir -p $resources
cp -r src/main/resources/* $resources

cp -r src/main/bash/continuum-* $bin

cp src/main/configuration/plexus.conf $runtime/conf/plexus.conf

#cp ../continuum-alarm-trigger/target/*.jar $lib
#cp ../continuum-cli/target/*.jar $lib
cp ../continuum-core/target/*.jar $lib
#cp ../continuum-fbi-trigger/target/*.jar $lib
cp ../continuum-hibernate-store/target/*.jar $lib
cp ../continuum-mail-notifier/target/*.jar $lib
cp ../continuum-standalone/target/*.jar $lib
#cp ../continuum-test/target/*.jar $lib
#cp ../continuum-tools/target/*.jar $lib
#cp ../continuum-xmlrpc-client/target/*.jar $lib
#cp ../continuum-xmlrpc-server/target/*.jar $lib
