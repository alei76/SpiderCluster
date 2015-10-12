#FROM ubuntu/14.04.1
FROM omartech/ubuntu-163-jdk8

ENV PORT 7154
ENV PASSWORD 123123

#install mysql
apt-get update &
        install -y mysql-server-5.6 &
        install -y mysql-client-5.6 



#switch dir
WORKDIR /spiderserver

#ADD sql file
ADD spidercluster.sql .
ADD my.cnf /etc/mysql/

#load tables
mysql -uroot -p{PASSWORD} -e "create database spidercluster default charset='utf8'";
mysql -uroot -p{PASSWORD} spidercluster < spidercluster.sql

#copy jar to docker
ADD spiderserver.jar .
ADD run.sh .

EXPOSE PORT

ENTRYPOINT ["/spider-server/run.sh"]

