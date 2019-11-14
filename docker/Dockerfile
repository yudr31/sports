FROM openjdk:8
MAINTAINER yuderen yuderen6312@163.com

COPY sports-1.0-SNAPSHOT.jar /home/sports.jar

WORKDIR /home

EXPOSE 9999

CMD ["java", "-jar", "-Pspring.profiles.active=dev", "sports.jar"]