#!/usr/bin/env bash

JAVA_VER=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | sed '/^1\./s///' | cut -d'.' -f1)

STAINLESS=stainless-0.1.0-93dbd33.jar
#STAINLESS=stainless-scalac-standalone-0.2.2.jar

if [ $JAVA_VER != 8 ]; then
  echo "You are using Java $JAVA_VER. It is recommended to use version 8."
fi

if [ "$1" == "" ]; then
  echo "Missing path parameter. Usage: stainless.sh <path to code>"
  echo "Taking default value '.'"
fi

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
CODE_DIR="modified"

PATH=$JAVA_HOME/bin:$PATH; java \
  -jar $DIR/../lib/$STAINLESS \
  $(find $1 -type f -name *.scala | tr '\n' ' ')
