#! /bin/bash

rm -rf spider-server/gen-java
rm -rf spider-client/gen-java

DIR=`pwd`
FILE="${DIR}/data.thrift"

(cd spider-client && thrift -gen java ${FILE})
(cd spider-server&& thrift -gen java ${FILE})



if [[ $# -ne 0 ]]; then
    (cd thrift-router && ./gen.sh)
fi
