FROM openjdk:8u141-jdk

RUN mkdir /opt/log
EXPOSE 8443
CMD ["java", "-jar", "-Dcas.log.dir=/opt/log/", "/opt/gumartinm-cas-2.0-SNAPSHOT.war"]

COPY ./target/*.war /opt/
