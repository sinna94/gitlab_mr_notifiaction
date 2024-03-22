FROM openjdk:21

COPY ./build/libs/ /app

WORKDIR /app

CMD java -jar ./gitlab_mr_notification.jar
