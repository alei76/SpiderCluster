FROM ubuntu:14.04
#FROM omartech/ubuntu-163-jdk8

RUN mkdir /spider-client

WORKDIR /spider-client

ADD spider-client-2.3-SNAPSHOT-jar-with-dependencies.jar /spider-client/

ADD run.sh /spider-client/

ENV SERVER 127.0.0.1
ENV PORT 7154
ENV LOCALSTORE localstore
ENV TIMESPAN 5

#CMD ["java", "-version"]
ENTRYPOINT ["/spider-client/run.sh"]   #ok

#CMD ["java", "-jar", "spider-client-2.1-SNAPSHOT-jar-with-dependencies.jar", "-s", SERVER, "-p", PORT, "-d", LOCASTORE, "-t", TIMESPAN] #not ok
#CMD ["java", "-jar", "spider-client-2.1-SNAPSHOT-jar-with-dependencies.jar", "-s", "192.168.1.117", "-p", 7154] ok
