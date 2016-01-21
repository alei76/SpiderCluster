FROM ubuntu:14.04
RUN cp /etc/apt/sources.list   /etc/apt/sources.list-bak
RUN echo  'deb http://mirrors.163.com/ubuntu/ trusty main restricted universe multiverse' > /etc/apt/sources.list
RUN echo  'deb http://mirrors.163.com/ubuntu/ trusty-security main restricted universe multiverse' >> /etc/apt/sources.list
RUN echo  'deb http://mirrors.163.com/ubuntu/ trusty-updates main restricted universe multiverse' >> /etc/apt/sources.list
RUN echo  'deb http://mirrors.163.com/ubuntu/ trusty-proposed main restricted universe multiverse' >> /etc/apt/sources.list
RUN echo  'deb http://mirrors.163.com/ubuntu/ trusty-backports main restricted universe multiverse' >> /etc/apt/sources.list
RUN echo  'deb-src http://mirrors.163.com/ubuntu/ trusty main restricted universe multiverse' >> /etc/apt/sources.list
RUN echo  'deb-src http://mirrors.163.com/ubuntu/ trusty-security main restricted universe multiverse' >> /etc/apt/sources.list
RUN echo  'deb-src http://mirrors.163.com/ubuntu/ trusty-updates main restricted universe multiverse' >> /etc/apt/sources.list
RUN echo  'deb-src http://mirrors.163.com/ubuntu/ trusty-proposed main restricted universe multiverse' >> /etc/apt/sources.list
RUN echo  'deb-src http://mirrors.163.com/ubuntu/ trusty-backports main restricted universe multiverse' >> /etc/apt/sources.list
ENV DEBIAN_FRONTEND noninteractive 
RUN apt-get update -y 					&& \
    apt-get install software-properties-common -y 	&& \
    add-apt-repository ppa:webupd8team/java -y 		&& \
    apt-get update -y 					&& \
    echo debconf shared/accepted-oracle-license-v1-1 select true | debconf-set-selections && \
    apt-get install oracle-java8-installer -y 		&& \
    rm -rf /var/lib/apt/lists/*
ENV JAVA_HOME /usr/lib/jvm/java-8-oracle

#docker build -t omartech/ubuntu-163-jdk8 .
