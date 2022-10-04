FROM openjdk:17

COPY ./build/libs/ /app

WORKDIR /app

CMD java -jar ./gitlab_MR_notification.jar


