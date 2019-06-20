#!/usr/bin/env bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
CODE_DIR="modified"

JAVA_HOME=/usr/lib/jvm/java-8-openjdk; PATH=$JAVA_HOME/bin:$PATH; java \
  -jar $DIR/../lib/stainless-0.1.0-93dbd33.jar \
  $DIR/../src/main/scala/addition/$CODE_DIR/currency/CurrencyUnits.scala \
  $DIR/../src/main/scala/addition/$CODE_DIR/number/NumberType.scala
