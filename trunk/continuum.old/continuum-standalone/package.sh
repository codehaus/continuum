#!/bin/bash

version="alpha-1"

# Create a tarball
tarball=continuum-${version}.tar.gz
tar zcf $tarball -C generated-runtime .

if [ -d $HOME/public_html ]
then
  cp $tarball $HOME/public_html
fi

if [ -d $HOME/www_docs ]
then
  cp $tarball $HOME/www_docs
fi

