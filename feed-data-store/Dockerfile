FROM openjdk:11-jre-slim

RUN apt-get update

COPY ./target/feed-data-store.jar /

EXPOSE 8081

CMD ["java", "-jar", "feed-data-store.jar"]