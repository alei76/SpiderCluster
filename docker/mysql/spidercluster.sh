#!/bin/sh
docker run --name spidercluster-mysql -e MYSQL_ROOT_PASSWORD=spiderserver -d -v /opt/docker/mysql/mysql_conf/spidercluster.cnf:/etc/mysql/my.cnf -v /opt/docker/mysql/mysql_dbs/spidercluster_db:/var/lib/mysql -p 3311:3306 mysql:5.6

