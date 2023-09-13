# For Java 8, try this
 FROM openjdk:8-jdk-alpine

# setting timezone
ENV TZ=Asia/Jakarta
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# create 
WORKDIR /var/www/bjbs_umroh_services

# copy
COPY report-design /var/www/bjbs_umroh_services/

# For Java 11, try this
# FROM adoptopenjdk/openjdk11:alpine-jre

# cd /opt/app
WORKDIR /opt/app

# cp bjbs-haji-0.0.1-exec.jar /opt/app/app.jar
COPY bjbs-haji-0.0.1-exec.jar app.jar

# java -jar /opt/app/app.jar
ENTRYPOINT ["java","-jar","app.jar"]

