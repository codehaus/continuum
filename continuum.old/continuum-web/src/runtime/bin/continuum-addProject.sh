#!/bin/bash

projectName=$1
scmConnection=$2
nagEmailAddress=$3
version=$4

if [ -z "$projectName" ]
then
  read -p "Project name:" projectName
fi

if [ -z "$scmConnection" ]
then
  read -p "SCM url:" scmConnection
fi

if [ -z "$nagEmailAddress" ]
then
  read -p "Nag email address :" nagEmailAddress
fi

if [ -z "$version" ]
then
  read -p "Version:" version
fi

type="maven2"

echo "$projectName
$scmConnection
$nagEmailAddress
$version
$type
" | nc localhost 6789
