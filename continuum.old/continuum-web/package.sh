#!/bin/bash

dist=continuum-alpha-1

# Create a tarball
tgzball=$dist.tar.gz
tbzball=$dist.tar.bz2

rm -rf $dist
cp -r generated-runtime $dist

rm -rf $dist/apps/maven2/repository
rm $dist/apps/continuumweb/forms
rm $dist/apps/continuumweb/localization
rm $dist/apps/continuumweb/web

cp -r src/main/resources/forms $dist/apps/continuumweb/
cp -r src/main/resources/localization $dist/apps/continuumweb/
cp -r src/main/resources/web $dist/apps/continuumweb/

find $dist -name CVS -and -type d | xargs rm -rf "" 

tar zcf $tgzball $dist

tar jcf $tbzball $dist

if [ -d $HOME/public_html ]
then
  cp $tgzball $HOME/public_html
  cp $tbzball $HOME/public_html
fi

if [ -d $HOME/www_docs ]
then
  cp $tgzball $HOME/www_docs
  cp $tbzball $HOME/www_docs
fi

