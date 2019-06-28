#!/usr/bin/env bash

if [ "$1" == "" ]; then
  echo "Missing path parameter. Usage: stainless.sh <path to code>"
  echo "Taking default value '.'"
fi

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
CODE_DIR="modified"

PATH=$JAVA_HOME/bin:$PATH; java \
  -jar $DIR/../lib/stainless-0.1.0-93dbd33.jar \
  $(find $1 -type f -name *.scala | tr '\n' ' ')
