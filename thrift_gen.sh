#! /bin/bash

rm -rf spider-server/gen-java
rm -rf spider-client/gen-java
rm -rf spider-handler/gen-java

DIR=`pwd`
FILE="${DIR}/data.thrift"

(cd spider-client && thrift -gen java ${FILE})
(cd spider-server&& thrift -gen java ${FILE})
(cd spider-handler&& thrift -gen java ${FILE})

dbip=10.1.0.171
dbport=3306
dbuser=root
dbpassword=123123

if [[ $# -ne 0 ]]; then
  dbip=$1
  dbport=$2
  dbuser=$3
  dbpassword=$4
  echo "dump tables from "$dbip" with "$dbport" username: "$dbuser" password: "$dbpassword
fi
mysqldump -d -h$dbip -P$dbport -u$dbuser -p$dbpassword spidercluster > spidercluster.sql
