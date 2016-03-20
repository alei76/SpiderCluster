#!/bin/bash

#1 mkdir

if [ -d spider-deploy ]; then
	rm -rf spider-deploy
fi

mkdir spider-deploy

#2 client
(cd spider-client && mvn clean assembly:assembly -Dmaven.test.skip=true)
mkdir spider-deploy/spider-client
cp spider-client/target/spider-client-*-dependencies.jar spider-deploy/spider-client/

echo '------------client over -------------------'

#3 server
(cd spider-server && mvn clean assembly:assembly -Dmaven.test.skip=true)
mkdir spider-deploy/spider-server
cp spider-server/target/spider-server-*-dependencies.jar spider-deploy/spider-server/

echo '------------server over -------------------'

#4 readme

cp spidercluster.sql spider-deploy/

echo '-----------How to run this?----------------'
echo '1. start up the mysql db'
echo '   * create a database named with "spidercluster"'
echo '   * load the table structure into database with "mysql -uxxx -p spidercluster < spidercluster.sql"'
echo '2. run spider-server'
echo '   -help can show args about server'
echo '3. run spider-client'
echo '   -help can show args about client'


