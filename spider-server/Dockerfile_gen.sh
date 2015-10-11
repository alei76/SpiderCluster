#!/bin/sh

mvn clean assembly:assembly -Dmaven.test.skip=true -U


#mkdir docker

#(cd docker && touch Dockerfile)

(cd target && cp spider-server-2.0-SNAPSHOT.jar ../docker/spider-server.jar)



#1. 生成jar
#2. 拷贝到docker
#3. 生成Dockerfile

