#!/bin/bash

set -e

rm -rf target
m2 modello:java modello:jpox modello:prevayler resources:resources
#javac -d target/classes `find target/generated-sources -type f -and -name \*.java -and -not -name \*Store.java`
#echo "Rebuild the project with your IDE and then press return"
#read
cp -r target/generated-sources/* target/classes
m2 compiler:compile jpox:enhance resources:testResources compiler:testCompile surefire:test jar:jar jar:install
