import ubuntu/14.04.1

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
ADD spiderserver.jar .


#load tables
mysql -uroot -p{PASSWORD} -e "create database spidercluster default charset='utf8'";
mysql -uroot -p spidercluster < spidercluster.sql


CMD java -jar spiderserver.jar -p PORT 

EXPOSE PORT


