FROM openjdk:11-jre-slim

RUN apt-get update

COPY ./target/feed-json-transformer-app.jar /

EXPOSE 8080

CMD ["java", "-jar", "feed-json-transformer-app.jar"]

