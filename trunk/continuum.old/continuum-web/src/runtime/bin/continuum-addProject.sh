#!/bin/bash

url=$1

if [ -z "$url" ]
then
  read -p "url:" url
fi

echo "$url
" | nc localhost 6790
