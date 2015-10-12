#FROM ubuntu/14.04.1
FROM omartech/ubuntu-163-jdk8

MAINTAINER Sonyfe25cp "sonyfe25cp@gmail.com"

ENV DBP 127.0.0.1:3306
ENV DBU root
ENV DBPW 123123

#install mysql
RUN apt-get update && \
    apt-get install -y  mysql-server-5.6 \
        		mysql-client-5.6 \
			supervisor
#switch dir
RUN mkdir /spiderserver
RUN mkdir /spiderstore
WORKDIR /spiderserver

#ADD sql file
ADD spidercluster.sql /spiderserver/
ADD my.cnf /etc/mysql/

RUN /etc/init.d/mysql start -safe_skip-grant-tables && \
    /usr/bin/mysql -uroot -e "update mysql.user set password=PASSWORD('$DBPW') where user = 'root';" && \
    /usr/bin/mysql -uroot -e "create database spidercluster default charset='utf8';" && \
    /usr/bin/mysql -uroot spidercluster < spidercluster.sql

RUN mkdir -p /var/log/supervisor  
COPY supervisord.conf /etc/supervisord.conf

##copy jar to docker
ADD spider-server-2.1-SNAPSHOT-jar-with-dependencies.jar /spiderserver/
ADD run.sh /spiderserver/
#
#
#ENTRYPOINT ["/spider-server/run.sh"]

EXPOSE 3306 7154

#CMD ["/usr/bin/supervisord"] 
#ENTRYPOINT /usr/bin/supervisord && /spiderserver/run.sh
ENTRYPOINT /etc/init.d/mysql start && /spiderserver/run.sh

